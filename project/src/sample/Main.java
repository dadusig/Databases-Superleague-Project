package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("start.fxml"));
        primaryStage.setTitle("DataBases Lab Project");
        primaryStage.setScene(new Scene(root, 360, 370));
        primaryStage.getIcons().add(new Image("sample/icon.png"));
        primaryStage.setResizable(false);

        //primaryStage.initStyle(StageStyle.UNDECORATED);



        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
