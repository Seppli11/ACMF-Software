package ch.sebi.acmf.ui;

import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration;
import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration.TValue.TValueStringEnum;
import ch.sebi.acmf.data.Value;
import ch.sebi.acmf.undomanager.UndoManager;
import ch.sebi.acmf.undomanager.ValueChangeAction;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.Optional;

/**
 * Created by Sebastian on 28.05.2017.
 */
public class ValueCell extends ListCell<Value> {
    Pane root;
    FlowPane intPane, enumPane;

    Label name;

    //int
    Slider slider;
    Label sliderLabel;

    //enum
    ChoiceBox<TValueStringEnum> enumCB;
    Label enumValue;

    Value lastItem;
    ChangeListener<TValueStringEnum> lastListener;

    Button nullBtn;
    ChangeListener<Boolean> undefinedListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            nullBtn.setDisable(newValue);
        }
    };

    private boolean setToUndefined = false;

    public ValueCell() {
        root = new Pane();

        name = new Label();
        name.setMinWidth(100);

        intPane = new FlowPane();
        intPane.layoutXProperty().bind(name.widthProperty().add(5));

        slider = new Slider();
        slider.setMinorTickCount(1);
        slider.setShowTickLabels(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderLabel.setText("" + Math.round(newValue.intValue()));
            nullBtn.setDisable(false);
            if(!slider.isValueChanging() && getItem() != null && !setToUndefined) {
				if(oldValue == newValue) return;
				Template t  = ACMFMainController.acmfMainController.getTimeline_panel().getSelectedTemplate();
 				UndoManager.MAIN.add(new ValueChangeAction(null, t, getItem().valueProperty(), getItem().undefinedProperty()));
			}
			setToUndefined = false;
		});

        slider.valueChangingProperty().addListener((observable, wasChanging, isNowChanging) -> {
        	if(isNowChanging) {
				Template t  = ACMFMainController.acmfMainController.getTimeline_panel().getSelectedTemplate();
				UndoManager.MAIN.add(new ValueChangeAction(null, t, getItem().valueProperty(), getItem().undefinedProperty()));
			}
		});

        sliderLabel = new Label();
        intPane.getChildren().addAll(slider, sliderLabel);
        intPane.prefWidthProperty().bind(slider.widthProperty().add(sliderLabel.widthProperty()).add(10));

        //enum
        enumPane = new FlowPane();
        enumPane.layoutXProperty().bind(name.widthProperty().add(5));
        enumCB = new ChoiceBox();
        enumValue = new Label();
        enumValue.setMinWidth(40);


        enumPane.getChildren().addAll(enumCB, enumValue);
        enumPane.prefWidthProperty().bind(enumCB.widthProperty().add(enumValue.widthProperty()).add(10));

        nullBtn = new Button("null");
        nullBtn.setDisable(true);
        nullBtn.setOnAction(event -> {
			setToUndefined = true;
			UndoManager.MAIN.add(new ValueChangeAction(null, null, getItem().valueProperty(), getItem().undefinedProperty()));
			getItem().setUndefined();
            nullBtn.setDisable(true);
            enumCB.setValue(null);
            sliderLabel.setText("-1");
		});
        nullBtn.layoutXProperty().bind(intPane.layoutXProperty().add(intPane.widthProperty()).add(10));


        root.getChildren().addAll(name, intPane, enumPane, nullBtn);

        setGraphic(root);
    }

    @Override
    protected void updateItem(Value item, boolean empty) {
        super.updateItem(item, empty);

        intPane.setVisible(false);
        enumPane.setVisible(false);
        name.textProperty().unbind();
        name.setText("");
        nullBtn.setVisible(item != null);
        if(item == null) return;
        if(lastItem == null) lastItem = item;


        name.textProperty().bind(item.getTValue().nameProperty());

        //slider unbinding
        slider.valueProperty().unbindBidirectional(lastItem.valueProperty());
       // sliderLabel.textProperty().unbindBidirectional(lastItem.valueProperty());

        if(lastListener != null) {
			enumCB.getSelectionModel().selectedItemProperty().removeListener(lastListener);
		}
		lastItem.valueProperty().removeListener(lastValueListener);

        enumValue.textProperty().unbindBidirectional(lastItem.valueProperty());

        lastItem.undefinedProperty().removeListener(undefinedListener);
        item.undefinedProperty().addListener(undefinedListener);
        nullBtn.setDisable(item.isUndefined());

        if(item.getTValue().getType() == DeviceConfiguration.TValueType.Int) {
            intPane.setVisible(true);
            slider.valueProperty().bindBidirectional(item.valueProperty());
            slider.setMin(item.getTValue().getDisplayMin());
            slider.setMax(item.getTValue().getDisplayMax());
            //sliderLabel.textProperty().bindBidirectional(item.valueProperty(), new NumberStringConverter());
        } else if(item.getTValue().getType() == DeviceConfiguration.TValueType.Enum) {
            enumPane.setVisible(true);
            enumCB.setItems(item.getTValue().getStringEnums());
            enumCB.getSelectionModel().select(-1);
            enumCB.getItems().stream().filter(tValueStringEnum -> tValueStringEnum.getValue() == item.getValue()).findFirst().ifPresent(tValueStringEnum -> {
                enumCB.getSelectionModel().select(tValueStringEnum);
            });

            enumValue.textProperty().bindBidirectional(item.valueProperty(), new NumberStringConverter());
			item.valueProperty().addListener(lastValueListener);
            lastListener = new ChangeListener<TValueStringEnum>() {
                private Value value = item;
                @Override
                public void changed(ObservableValue<? extends TValueStringEnum> observable, TValueStringEnum oldTValueStringEnum, TValueStringEnum newTValueStringEnum) {
                    if(newTValueStringEnum == null) return;
					if(oldTValueStringEnum == newTValueStringEnum) return;
					UndoManager.MAIN.add(new ValueChangeAction(null, null, getItem().valueProperty(), getItem().undefinedProperty()));
					value.setValue(newTValueStringEnum.getValue());
                    nullBtn.setDisable(false);
                    //enumValue.setText("" + newTValueStringEnum.getAverage());
                }
            };
            enumCB.getSelectionModel().selectedItemProperty().addListener(lastListener);
        } else
            throw new IllegalStateException("TValue type '" + item.getTValue().getType().name() + " isn't suported!");

        lastItem = item;
    }
    private ChangeListener lastValueListener = new ChangeListener<Integer>() {

		@Override
		public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
			if(oldValue == newValue) return;
			Optional<TValueStringEnum> optional = enumCB.getItems().stream().filter(tValueStringEnum -> tValueStringEnum.getValue() == newValue).findFirst();
			if(optional.isPresent()) {
				enumCB.getSelectionModel().select(optional.get());
			} else {
				enumCB.getSelectionModel().select(null);
			}
		}
	};
}
