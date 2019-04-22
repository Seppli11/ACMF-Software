package ch.sebi.acmf.ui;

import ch.sebi.acmf.data.*;
import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration;
import ch.sebi.acmf.deviceconfiguration.DeviceConfigurationManager;
import ch.sebi.acmf.serial.ACMF;
import ch.sebi.acmf.undomanager.*;
import ch.sebi.acmf.utils.SettingsManager;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.javafx.binding.BidirectionalBinding;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.File;
import java.util.Optional;
import java.util.Properties;

public class ACMFMainController {
	public static ACMFMainController acmfMainController;

    @FXML
    private Menu export_menu;

    @FXML
    private ListView<Song> song_listview;


    @FXML
    private HBox templates_hbox;

    @FXML
    private ScrollPane timeline_scrollpane;
    @FXML
    private TimelinePane timeline_panel;


    @FXML
    private ListView<Value> midi1;
    //@FXML private ListView<Integer> midi1_values;

    @FXML
    private ListView<Value> midi2;

    @FXML
    private Menu live_mode_menu;

    private Stage stage;

    @FXML
    private Label midi1_label;

    @FXML
    private Label midi2_label;

    @FXML
    private CheckMenuItem connectedCb;

    public void setState(Stage s) {
        this.stage = s;
    }

    @FXML
    private void initialize() {
    	acmfMainController = this;
        midi1.setCellFactory(param -> new ValueCell());
        midi2.setCellFactory(param -> new ValueCell());
        timeline_panel.setup(midi1, midi2, midi1_label, midi2_label);
        timeline_scrollpane.setFitToHeight(true);

        song_listview.setCellFactory(lv -> {
            TextFieldListCell<Song> cell = new TextFieldListCell<>();
            cell.setConverter(new Song.SongConverter());
            return cell;
        });

        if (!SettingsManager.SONG_DIRECTORY.equals("")) {
            song_listview.getItems().setAll(SongSet.currentSongSet.songs);
        }

        song_listview.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (song_listview.getSelectionModel().getSelectedItem() != null) {
                templates_hbox.setDisable(false);
                timeline_panel.changeSong(song_listview.getSelectionModel().getSelectedItem());
            } else {
                timeline_panel.changeSong(null);
                templates_hbox.setDisable(true);
            }
        });

        DeviceConfigurationManager.deviceConfigurations.addListener((InvalidationListener) c -> {
            timeline_panel.changeSong(song_listview.getSelectionModel().getSelectedItem());
            midi1.refresh();
            midi2.refresh();
        });

        MenuItem searchingTextItem = new MenuItem("searching ACMF...");
        searchingTextItem.setDisable(true);
        ACMF.getAcmfList().addListener((ListChangeListener<? super ACMF>) c -> {
            Platform.runLater(() -> {
                export_menu.getItems().clear();
                export_menu.getItems().add(searchingTextItem);
                for (ACMF acmf : c.getList()) {
                    export_menu.getItems().add(new ACMFMenuItem(acmf));
                }
            });
        });
        //setting up thread which checks if new devices where found
        new Thread(() -> {
            searchingTextItem.setVisible(true);
            while (true) {
                if(connectToAcmf())
                    ACMF.searchACMFs();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //redo/undo stuff
    @FXML
    private void undo() {
        //ChangeManager.MAIN.undo();
        UndoManager.MAIN.undo();
    }

    @FXML
    private void redo() {
       // ChangeManager.MAIN.redo();
        UndoManager.MAIN.redo();
    }


    //template stuff
    @FXML
    private void addTemplateBtn() {
        TextInputDialog dialog = new TextInputDialog("Name");
        dialog.setTitle("Template Name");
        dialog.setHeaderText("Was ist der Name des Templates?");
        dialog.setContentText("Template Name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            Template t = new Template(result.get());
            timeline_panel.getTemplates().add(t);
            UndoManager.MAIN.add(new TemplateAddAction(song_listview.getSelectionModel().getSelectedItem(), t));
            //ChangeManager.MAIN.add(new ListAddChange<>(timeline_panel.getTemplates(), t));
        }
    }

    @FXML
    private void removeTemplateBtn() {
        UndoManager.MAIN.add(new TemplateRemoveAction(song_listview.getSelectionModel().getSelectedItem(), timeline_panel.getSelectedTemplate()));
        timeline_panel.removeSelectedNode();
    }

    private Dialog<Pair<String, Pair<DeviceConfiguration, DeviceConfiguration>>> getSongDialog
            (@NotNull String name, @Nullable DeviceConfiguration dc1, @Nullable DeviceConfiguration dc2, boolean editDC) {
        Dialog<Pair<String, Pair<DeviceConfiguration, DeviceConfiguration>>> dialog = new Dialog<>();
        dialog.setTitle("Songname");
        dialog.setHeaderText("Songeingstellungen:");

        ButtonType cancleButton = new ButtonType("Abbrechen");
        ButtonType createButton = new ButtonType("Ok");
        dialog.getDialogPane().getButtonTypes().addAll(cancleButton, createButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField songName = new TextField(name);
        songName.setPromptText("Songname");

        ChoiceBox<DeviceConfiguration> dc1CB = new ChoiceBox<>(DeviceConfigurationManager.deviceConfigurations);
        ChoiceBox<DeviceConfiguration> dc2CB = new ChoiceBox<>(DeviceConfigurationManager.deviceConfigurations);
        if(!editDC) {
            dc1CB.setDisable(true);
            dc2CB.setDisable(true);
        }

        Platform.runLater(() -> {
            if (dc1 != null)
                dc1CB.getSelectionModel().select(dc1);
            else
                dc1CB.getSelectionModel().select(0);
            if (dc2 != null)
                dc2CB.getSelectionModel().select(dc2);
            else
                dc2CB.getSelectionModel().select(0);
        });

        grid.add(new Label("Songname:"), 0, 0);
        grid.add(songName, 0, 1);

        grid.add(new Label("Midi 1 Gerät:"), 2, 0);
        grid.add(dc1CB, 2, 1);
        grid.add(new Label("Midi 2 Gerät:"), 3, 0);
        grid.add(dc2CB, 3, 1);

        Node createButtonN = dialog.getDialogPane().lookupButton(createButton);
        createButtonN.setDisable(!(!songName.getText().trim().isEmpty() && !dc1CB.getSelectionModel().isEmpty() && !dc2CB.getSelectionModel().isEmpty()));

        songName.textProperty().addListener((observable, oldValue, newValue) -> {
            createButtonN.setDisable(!(!songName.getText().trim().isEmpty() && !dc1CB.getSelectionModel().isEmpty() && !dc2CB.getSelectionModel().isEmpty()));
        });

        dc1CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            createButtonN.setDisable(!(!songName.getText().trim().isEmpty() && !dc1CB.getSelectionModel().isEmpty() && !dc2CB.getSelectionModel().isEmpty()));
        });

        dc2CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            createButtonN.setDisable(!(!songName.getText().trim().isEmpty() && !dc1CB.getSelectionModel().isEmpty() && !dc2CB.getSelectionModel().isEmpty()));
        });


        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> songName.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButton) {
                return new Pair<>(songName.getText(), new Pair<>(dc1CB.getValue(), dc2CB.getValue()));
            } else {
                return new Pair<>(null, null);
            }
        });
        return dialog;
    }

    //Songlist
    @FXML
    private void addSongBtn() {
        Optional<Pair<String, Pair<DeviceConfiguration, DeviceConfiguration>>> result = getSongDialog("", null, null, true).showAndWait();
        result.ifPresent(stringPairPair -> {
            if (stringPairPair.getKey() == null) return;
            Song s = new Song(result.get().getKey(), result.get().getValue().getKey(), result.get().getValue().getValue());
            song_listview.getItems().add(s);
            song_listview.getSelectionModel().select(s);
            //ChangeManager.MAIN.add(new ListAddChange<Song>(song_listview.getItems(), s));
            UndoManager.MAIN.add(new SongAddAction(s, song_listview));
        });
    }

    @FXML
    private void removeSongBtn() {
        if (song_listview.getSelectionModel().getSelectedItem() != null) {
            Song songToRemove = (Song) song_listview.getSelectionModel().getSelectedItem();
            //ChangeManager.MAIN.add(new ListRemoveChange<Song>(song_listview.getItems(), songToRemove));
            UndoManager.MAIN.add(new SongRemoveAction(songToRemove));
            song_listview.getItems().remove(songToRemove);

            SongSet.currentSongSet = new SongSet(song_listview.getItems());
        }
    }

    @FXML
    private void editSongBtn() {
        if (song_listview.getSelectionModel().getSelectedItem() != null) {
            //song_listview.edit(song_listview.getSelectionModel().getSelectedIndex());
            Song s = song_listview.getSelectionModel().getSelectedItem();
            Optional<Pair<String, Pair<DeviceConfiguration, DeviceConfiguration>>> result = getSongDialog(s.getName(), s.getMidi1DeviceConfiguration(), s.getMidi2DeviceConfiguration(), false).showAndWait();
            result.ifPresent(stringPairPair -> {
                if(result.get().getKey() == null || result.get().getValue().getKey() == null || result.get().getValue().getValue() == null)
                    return;
                s.setName(result.get().getKey());
                /*s.setMidi1DeviceConfiguration(result.get().getValue().getKey());
                s.setMidi2DeviceConfiguration(result.get().getValue().getValue());*/
                song_listview.refresh();
            });
        }
    }

    @FXML
    private void moveSongUpBtn() {
        if (song_listview.getSelectionModel().getSelectedItem() != null) {
            Song s = song_listview.getSelectionModel().getSelectedItem();
        	UndoManager.MAIN.add(new SongChangeOrderAction(s, song_listview.getItems()));
            int index = song_listview.getSelectionModel().getSelectedIndex();

            song_listview.getItems().remove(s);

            int newIndex = index - 1;
            if(newIndex < 0) newIndex = 0;

            song_listview.getItems().add(newIndex, s);

            song_listview.getSelectionModel().select(newIndex);
            SongSet.currentSongSet = new SongSet(song_listview.getItems());
        }
    }

    @FXML
    private void moveSongDownBtn() {
        if (song_listview.getSelectionModel().getSelectedItem() != null) {
            Song s = song_listview.getSelectionModel().getSelectedItem();
			UndoManager.MAIN.add(new SongChangeOrderAction(s, song_listview.getItems()));
			int index = song_listview.getSelectionModel().getSelectedIndex();

            song_listview.getItems().remove(s);

            int newIndex = index + 1;
            if(newIndex >= song_listview.getItems().size()) newIndex = song_listview.getItems().size();

            song_listview.getItems().add(newIndex, s);

            song_listview.getSelectionModel().select(newIndex);
			SongSet.currentSongSet = new SongSet(song_listview.getItems());
		}
    }

    @FXML
    private void saveSongs() {
        if (SettingsManager.SONG_DIRECTORY.equals("")) {
            /*DirectoryChooser dc = new DirectoryChooser();
            File f = dc.showDialog(timeline_panel.getScene().getWindow());
            if (f == null) return;
            SettingsManager.preferences.put(SettingsManager.SONG_DIRECTORY_ID, f.getAbsolutePath());
            SettingsManager.reload();*/
            saveSongAs();
            return;
        }

        SongSet ss = new SongSet(song_listview.getItems());
        SongSet.save(ss, new File(SettingsManager.SONG_DIRECTORY));

        //build and show save notification
        ImageView img = new ImageView("/tick.png");
        img.setFitWidth(60);
        img.setFitHeight(60);
        img.setSmooth(true);
        Notifications saveNotificatinoBuilder = Notifications.create()
                .title("Projekt gespeichert!")
                .text("Das Projekt wurde gespeichert.")
                .graphic(img)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BASELINE_RIGHT);
        saveNotificatinoBuilder.show();

    }

    @FXML
    private void saveSongAs() {
        DirectoryChooser dc = new DirectoryChooser();
        if(!SettingsManager.SONG_DIRECTORY.equals(""))
            dc.setInitialDirectory(new File(SettingsManager.SONG_DIRECTORY));
        File f = dc.showDialog(timeline_panel.getScene().getWindow());
        if (f == null) return;
        SettingsManager.preferences.put(SettingsManager.SONG_DIRECTORY_ID, f.getAbsolutePath());
        SettingsManager.reload();

        saveSongs();
    }

	public ListView<Song> getSong_listview() {
		return song_listview;
	}

	public TimelinePane getTimeline_panel() {
		return timeline_panel;
	}

	public boolean connectToAcmf() {
        return connectedCb.isSelected();
    }
}
