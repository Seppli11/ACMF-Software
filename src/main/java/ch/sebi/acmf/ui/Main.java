package ch.sebi.acmf.ui;

import java.awt.SplashScreen;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.sebi.acmf.utils.ExceptionHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private static Logger logger = LogManager.getLogger(Main.class.getName());

	@Override
	public void start(Stage primaryStage) throws Exception{
		try {
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
		} catch(Throwable t) {
			logger.error("An exception occured: ", t);
			ACMFMainController.showErrorDialog(t);
		}
	}


	public static void main(String[] args) throws IOException, InterruptedException {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
		launch(args);
	}

	private static void createSplashscreen() {
		SplashScreen sp = SplashScreen.getSplashScreen();
	}
}
