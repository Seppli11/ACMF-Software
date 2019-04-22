package ch.sebi.acmf.ui;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.SongSet;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.data.Value;
import ch.sebi.acmf.serial.ACMF;
import ch.sebi.acmf.serial.cmd.SendSongsOk;
import ch.sebi.acmf.serial.cmd.SendSongsRequest;
import ch.sebi.acmf.utils.NotificationUtils;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


/**
 * Created by Sebastian on 20.07.2017.
 */
public class ACMFMenuItem extends MenuItem {
    private ACMF acmf;
    private Alert alert;
    private ProgressBar progressBar;
    private Label label;

    public ACMFMenuItem(ACMF acmf)  {
        super(acmf.getPort().getDescriptivePortName());
        this.acmf = acmf;

        setOnAction(event -> {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportieren zu " + acmf.getPort().getDescriptivePortName());
            //alert.setHeaderText("Songs werden exportiert zum ACMF '" + acmf.getPort().getDescriptivePortName() + "'.");
            VBox vBox = new VBox();
            progressBar = new ProgressBar(0);
            label = new Label(">Wartet auf ACMF");
            vBox.getChildren().setAll(
                    new Label("Songs werden exportiert zum ACMF '" + acmf.getPort().getDescriptivePortName() + "'."),
                    new Separator(Orientation.HORIZONTAL),
                    progressBar,
                    label);
            alert.setGraphic(vBox);
            alert.show();
            acmf.send(new SendSongsRequest(SongSet.currentSongSet), new SendSongsOk(), (cmd, acmf1) -> {
                acmf.sendSongSet(SongSet.currentSongSet, this);
            });

        });
    }

    public void setProgress(double progress) {
        if(alert != null && progressBar != null) {
            Platform.runLater(() -> progressBar.setProgress(progress));
        }
    }

    public void setExportingSong(Song s) {
        if(alert != null && label != null) {
            Platform.runLater(() -> label.setText(">" + s.getName() + ""));
        }
    }

    public void setExportingTemplate(Song s, Template t) {
        if(alert != null && label != null) {
            Platform.runLater(() -> label.setText(">" + s.getName() + ">" + t.getName() + ""));
        }
    }

    public void setExportingValues(Song s, Template t) {
        if(alert != null && label != null) {
            Platform.runLater(() -> label.setText(">" + s.getName() + ">" + t.getName() +
                    ">Values (Kanal 1: " + t.getMidi1Values().size() +  ", Kanal 2: " + t.getMidi2Values().size() + ")"));
        }
    }
}
