package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.constance;

public class Main extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/UI/scenes/index.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
//			primaryStage.setScene(constance.getScenes("index"));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
