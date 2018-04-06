package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sample.Controllers.MainController;

public class Main extends Application {
    private static final int NUMBER_X_TILES=MainController.SIZE_OF_FIELD;
    private static final int NUMBER_Y_TILES=MainController.SIZE_OF_FIELD;
    private static final int WIDTH=NUMBER_X_TILES*30+150;
    private static final int HEIGHT=NUMBER_Y_TILES*30+10;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/FXML/MainWindow.fxml"));
        BorderPane borderPane = loader.load();
        borderPane.setPrefHeight(HEIGHT);
        borderPane.setPrefWidth(WIDTH);
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
