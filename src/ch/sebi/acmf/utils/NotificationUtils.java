package ch.sebi.acmf.utils;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Created by Sebastian on 15.07.2017.
 */
public class NotificationUtils {
    public static void showTickNotification(String title, String text) {
        //build and show save notification
        ImageView img = new ImageView("/tick.png");
        img.setFitWidth(60);
        img.setFitHeight(60);
        img.setSmooth(true);
        Notifications saveNotificatinoBuilder = Notifications.create()
                .title(title)
                .text(text)
                .graphic(img)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BASELINE_RIGHT);
        saveNotificatinoBuilder.show();
    }

    public static void showErrorNotification(String title, String text) {
        //build and show save notification
        /*ImageView img = new ImageView("/tick.png");
        img.setFitWidth(60);
        img.setFitHeight(60);
        img.setSmooth(true);*/
        Notifications saveNotificatinoBuilder = Notifications.create()
                .title(title)
                .text(text)
                //.graphic(img)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BASELINE_RIGHT);
        saveNotificatinoBuilder.showError();
    }
}
