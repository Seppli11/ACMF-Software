package ch.sebi.acmf.ui;

import ch.sebi.acmf.data.SongSet;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.data.Value;
import ch.sebi.acmf.serial.ACMF;
import ch.sebi.acmf.serial.cmd.*;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static ch.sebi.acmf.utils.ByteUtils.B;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = (Parent) loader.load();
        primaryStage.setTitle("ACMF Software");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        ACMFMainController controller = (ACMFMainController) loader.getController();
        controller.setState(primaryStage);
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);

        /*ACMF.searchACMFs();
        Thread.sleep(2000);

        ACMF acmf = ACMF.getAcmfList().get(0);

        //ACMF acmf = new ACMF(SerialPort.getCommPorts()[1]);
        acmf.send(new Ping());
        acmf.send(new SendSongsRequest(SongSet.currentSongSet), new SendSongsOk(), (cmd, acmf1) -> {
            acmf1.send(new SendSong(SongSet.currentSongSet.songs.get(0)));
            byte i = 0;
            for(Template t : SongSet.currentSongSet.songs.get(0).getTemplates()) {
                acmf1.send(new SendTemplate(t, true, i++));
                acmf1.send(new SendValue(t.getMidi1Values().stream().collect(Collectors.toList())));

                acmf1.send(new SendTemplate(t, false, i++));
                acmf1.send(new SendValue(t.getMidi2Values().stream().collect(Collectors.toList())));
            }
        });

        SerialPort p = SerialPort.getCommPort("COM1");
        p.openPort();
        p.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if(event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
                byte[] bytes = new byte[p.bytesAvailable()];
                System.out.println(Arrays.toString(bytes));
            }
        });
        p.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        OutputStream out = p.getOutputStream();
        out.write(new byte[]{0, 1});
        out.flush();*/
    }
}
