package ch.sebi.acmf.ui.exportdialog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Sebastian on 20.07.2017.
 */
public class ExportDialogController extends Stage implements Initializable {
    @FXML private ProgressBar export_progressbar;
    @FXML private TextArea log_ta;

    public ExportDialogController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("export_dialog.fxml"));
        loader.setController(this);

        try {
            Scene s = new Scene((Parent) loader.load());
            setScene(s);
            setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        export_progressbar.setProgress();
    }

    public void setExportPercent(double p) {
        export_progressbar.setProgress(p);
    }

    public void log(String s) {
        log_ta.setText(log_ta.getText() + s + "\n");
    }
}
