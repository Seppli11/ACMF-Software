package ch.sebi.acmf.ui;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ACMFStarter extends Application {
	private static Logger logger = LogManager.getLogger(ACMFStarter.class.getName());

	@Override
	public void start(Stage primaryStage) throws Exception{
		try {
			FXMLLoader loader = new FXMLLoader(ACMFStarter.class.getResource("/main.fxml"));
			Parent root = (Parent) loader.load();
			primaryStage.setTitle("ACMF Software");
			primaryStage.setScene(new Scene(root, 800, 600));
			primaryStage.setOnCloseRequest(event -> {
				Platform.exit();
				System.exit(0);
			});
			primaryStage.getIcons().add(getImage());
			ACMFMainController controller = (ACMFMainController) loader.getController();
			controller.setStage(primaryStage);
			primaryStage.show();
		} catch(Throwable t) {
			logger.error("An exception occured: ", t);
			ACMFMainController.showErrorDialog(t);
		}
	}

	private static Image getImage() {
		InputStream in1 = ACMFStarter.class.getResourceAsStream("/icon.png");
		if (in1 != null) {
			logger.info("Icon loaded from pacakge");
			return new Image(in1);
		}
		logger.info("Icon loaded from icon path");
		return new Image("file:./icon.png");
	}

	public static void startApplication(String[] args) {
		launch(args);
	}

}
