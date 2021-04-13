package Application;

import org.jfree.ui.RefineryUtilities;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import transfer.dynamicChart;

public class Server extends Application {
	public void start(Stage primaryStage) {

		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/View/Server.fxml"));
			Scene scene = new Scene(root, 615, 528);
			scene.getStylesheets().add(getClass().getResource("/View/Server.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}