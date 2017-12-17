package ch.sebi.acmf.ui;

import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration;
import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.data.Value;
import ch.sebi.acmf.undomanager.TemplateChangeOrderAction;
import ch.sebi.acmf.undomanager.UndoManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.Comparator;
import java.util.stream.Collectors;


/**
 * Created by Sebastian on 25.05.2017.
 */
public class TimelinePane extends Pane {
    private ListProperty<Template> templates = new SimpleListProperty<>(FXCollections.observableArrayList());

    private DraggablePane focusedPane;
    private DraggablePane dragPane;

    private Song song;

    //value stuff
    private ListView<Value> midi1;

    private ListView<Value> midi2;

    private Label midi1Label, midi2Label;

    public TimelinePane() {
        templates.addListener((ListChangeListener.Change<? extends Template> c) -> {
            while (c.next()) {
                if(c.wasAdded()) {
                    int x = 0;
                    for(Node n : getChildren()) {
                        x+=n.getBoundsInParent().getWidth() + 5;
                    }

                    for (Template t : c.getAddedSubList()) {
                        DraggablePane dp = new DraggablePane(t, this);
                        getChildren().add(dp);
                        dp.setLayoutX(x + dp.getLayoutBounds().getWidth());
                    }
                }
                if(c.wasRemoved()) {
                    for (Template t : c.getRemoved()) {
                        for (Node n : getChildren()) {
                            DraggablePane dp = (DraggablePane) n;
                            if (dp.template == t) {
                                getChildren().remove(dp);
                                break;
                            }
                        }
                    }
                }
            }
            layoutChildren();
        });
    }

    public void setup(ListView<Value> midi1, ListView<Value> midi2, Label midi1Label, Label midi2Label) {
        this.midi1 = midi1;
        this.midi2 = midi2;
        this.midi1Label = midi1Label;
        this.midi2Label = midi2Label;
    }

    @Override
    protected void layoutChildren() {
       // System.out.println("hy");
        super.layoutChildren();
        ObservableList<Node> workingList = FXCollections.observableArrayList(getChildren());
        workingList.sort((o1, o2) -> {
            if(o1.getLayoutX() > o2.getLayoutX()) return 1;
            else if(o1.getLayoutX() < o2.getLayoutX()) return -1;
            else return 0;
        });
        getChildren().setAll(workingList);

        int x = 0;
        int i = 0;
        for(Node n : getChildren()) {
            DraggablePane dp = (DraggablePane) n;
            if(dp.indexProperty().get() != i) {
                dp.indexProperty().set(i);
            }
            i++;

            if(dp != dragPane) {
                dp.setLayoutX(x);
                dp.setLayoutY(getHeight() / 2 - n.getLayoutBounds().getHeight() / 2);
            }
            x+=dp.getWidth() + 5;
        }
	}

    public void setDragPane(DraggablePane dragNode) {
        this.dragPane = dragNode;
    }

    public ObservableList<Template> getTemplates() {
        return templates.get();
    }

    public ListProperty<Template> templatesProperty() {
        return templates;
    }

    public void removeSelectedNode() {
        if(focusedPane != null) {
            templates.remove(focusedPane.template);
        }
    }

    public void changeSong(Song s) {
        disableMidiPanel();
        if(s == null){
            getChildren().clear();
            return;
        }
        if(song != null) Bindings.unbindBidirectional(templatesProperty(), song.templatesProperty());
        getChildren().clear();
        song = s;
        Bindings.bindBidirectional(templatesProperty(), song.templatesProperty());

		midi1Label.setText(song.getMidi1DeviceConfiguration().getName());
		midi2Label.setText(song.getMidi2DeviceConfiguration().getName());
	}

    public void disableMidiPanel() {
        midi1.setDisable(true);
        midi2.setDisable(true);
    }

    public Template getSelectedTemplate() {
        return focusedPane.template;
    }

    private static BooleanProperty requestValueFocuse = new SimpleBooleanProperty();
    class DraggablePane extends Pane {
        private double mouseX, mouseY;

        private TimelinePane parent;

        public Label textNode;
        public TextField textField;

        public Template template;

        //private ValueChange<String> currentChange;

        private IntegerProperty index = new SimpleIntegerProperty(-1);
		private int oldIndex = 0;

        private boolean requestedFocuse;


        public DraggablePane(Template t, TimelinePane parent) {
            this.template = t;
            setPrefSize(100, 100);
            setFocusTraversable(true);
            setStyle("-fx-border-color:black;");
            focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    requestedFocuse = true;
                    requestValueFocuse.set(true);
                }
            });

            requestValueFocuse.addListener((observable, oldValue, newValue) -> {
                if(!newValue) return;
                if(requestedFocuse) {
                    requestedFocuse = false;
                    requestValueFocuse.set(false);
                    parent.getChildren().forEach(node -> node.setStyle("-fx-border-color:black;"));
                    setStyle("-fx-border-color:blue");
                    loadTemplate();
                }
            });


            this.parent = parent;
            setOnMousePressed(event -> {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                //setManaged(false);
                parent.setDragPane(this);
                oldIndex = indexProperty().get();
            });
            setOnMouseReleased(event -> {
                parent.setDragPane(null);
                parent.layoutChildren();
                if(oldIndex != indexProperty().get()) {
					UndoManager.MAIN.add(new TemplateChangeOrderAction(song, templates));
					templates.setAll(parent.getChildren().stream().map(node -> ((DraggablePane) node).template).collect(Collectors.toList()));
					parent.layoutChildren();
					disableMidiPanel();
				}
            });

            setOnMouseDragged(event -> {
                double deltaX = event.getSceneX() - mouseX;
                double deltaY = event.getSceneY() - mouseY;
                relocate(getLayoutX() + deltaX, getLayoutY() + deltaY);
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                parent.layoutChildren();
            });
            setOnMouseClicked(event -> {
                requestFocus();
                parent.focusedPane = this;
                if (event.getClickCount() == 2) {
                    if (textNode.isVisible()) {
                        textNode.setVisible(false);
                        textField.setDisable(false);
                        textField.setVisible(true);
                    } else {
                        textNode.setVisible(true);
                        textField.setDisable(true);

                        textField.setVisible(false);
                    }
                }
            });


            textNode = new Label();
            textNode.textProperty().bind(template.nameProperty());
            getChildren().add(textNode);
            textNode.relocate(5, textNode.getBoundsInParent().getHeight());
            textNode.setMaxWidth(90);

            textField = new TextField();
            getChildren().add(textField);
            textField.textProperty().bindBidirectional(template.nameProperty());
            textField.relocate(5, textNode.getBoundsInParent().getHeight());
            textField.setVisible(false);
            textField.setMaxWidth(90);
            textField.setOnAction(event -> {
                textNode.setVisible(true);
                textField.setDisable(true);
                textField.setVisible(false);
            });
        }

        public IntegerProperty indexProperty() {
            return index;
        }

        public void loadTemplate() {
            midi1.setDisable(false);
            midi2.setDisable(false);

            midi1.getItems().clear();
            midi1.getItems().setAll(template.getMidi1Values());

            for(DeviceConfiguration.TValue tv1 : song.getMidi1DeviceConfiguration().getTValuesList()) {
                boolean existsTv1 = false;
                for(Value v : template.getMidi1Values()) {
                    DeviceConfiguration.TValue tv2 = v.getTValue();
                    if(tv1.getType() == tv2.getType() && tv1.getName().equals(tv2.getName()) && tv1.getMin() == tv2.getMin() && tv1.getMax() == tv2.getMax()) { //tests if the list doesn't contain tv
                        existsTv1 = true;
                    }
                }
                if(!existsTv1) {
                    Value newV = new Value(tv1); //create new value from the TValue
                    midi1.getItems().add(newV);  //add it to the list
                    template.getMidi1Values().add(newV); //add it to the template
                }
            }

            midi2.getItems().clear();
            midi2.getItems().setAll(template.getMidi2Values());


            for(DeviceConfiguration.TValue tv1 : song.getMidi2DeviceConfiguration().getTValuesList()) {
                boolean existsTv1 = false;
                for(Value v : template.getMidi2Values()) {
                    DeviceConfiguration.TValue tv2 = v.getTValue();
                    if(tv1.getType() == tv2.getType() && tv1.getName().equals(tv2.getName()) && tv1.getMin() == tv2.getMin() && tv1.getMax() == tv2.getMax()) { //tests if the list doesn't contain tv
                        existsTv1 = true;
                    }
                }
                if(!existsTv1) {
                    Value newV = new Value(tv1); //create new value from the TValue
                    midi2.getItems().add(newV);  //add it to the list
                    template.getMidi2Values().add(newV); //add it to the template
                }
            }

        }
    }
}
