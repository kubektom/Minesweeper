package sample.Controllers;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MainController {

    @FXML
    BorderPane mainPane;

    @FXML
    Label flagLabel;

    @FXML
    Label bombLabel;

    public static final int SIZE_OF_FIELD = 20;

    private Tile[][] grid = new Tile[SIZE_OF_FIELD][SIZE_OF_FIELD];
    private ArrayList<Tile> bombList= new ArrayList<>();
    private ArrayList<Tile> nonBombList= new ArrayList<>();
    private IntegerProperty numberOfBombs=new SimpleIntegerProperty(0);
    private IntegerProperty numberOfFlags=new SimpleIntegerProperty(0);

    public MainController() {
    }

    @FXML
    void initialize() {
        bombLabel.textProperty().bind(numberOfBombs.asString());
        flagLabel.textProperty().bind(numberOfFlags.asString());
        mainPane.setCenter(createField());
    }

    private Pane createField() {

        Pane mainPanel = new Pane();

        for (int x = 0; x < SIZE_OF_FIELD; x++) {
            for (int y = 0; y < SIZE_OF_FIELD; y++) {

                Tile tile = new Tile(x, y, Math.random() < 0.1);
                if (tile.isBomb) {
                    bombList.add(tile);
                }else{
                    nonBombList.add(tile);
                }
                grid[x][y] = tile;
                mainPanel.getChildren().add(tile);

            }
        }
        numberOfBombs.set(bombList.size());
        numberOfFlags.set(bombList.size());
        for (int x = 0; x < SIZE_OF_FIELD; x++) {
            for (int y = 0; y < SIZE_OF_FIELD; y++) {

                int howManyBombsAround = 0;
                List<Tile> neighbors = getNeighbors(grid[x][y]);
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!grid[x][y].textLabel.getText().equals("")) {
                        if (neighbors.get(i).textLabel.getText().equals(""))
                            neighbors.get(i).click(MouseButton.PRIMARY);
                    }

                    if (neighbors.get(i).isBomb) {
                        howManyBombsAround = howManyBombsAround + 1;
                    }
                }

                if (howManyBombsAround != 0)
                    grid[x][y].textLabel.setText("" + howManyBombsAround);

                if (grid[x][y].isBomb) {
                    grid[x][y].textLabel.setText("X");
                }
            }
        }

        return mainPanel;
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        int[] positions = new int[]{
                -1, -1,
                -1, 0,
                -1, 1,
                0, -1,
                0, 1,
                1, -1,
                1, 0,
                1, 1
        };

        for (int i = 0; i < positions.length; i++) {
            int dx = positions[i];
            int dy = positions[++i];

            int newX = tile.x + dx;
            int newY = tile.y + dy;

            if (newX >= 0 && newX < SIZE_OF_FIELD
                    && newY >= 0 && newY < SIZE_OF_FIELD) {
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }


    private class Tile extends StackPane {
        private int x, y;
        private boolean isClicked = false;
        Label textLabel = new Label("");
        Label flagLabel = new Label("F");
        private boolean isBomb=false;
        private boolean isFlag=false;
        Rectangle border = new Rectangle(30, 30);

        Tile(int x, int y, boolean isBomb) {
            this.x = x;
            this.y = y;
            textLabel.setWrapText(true);
            textLabel.setFont(Font.font(25));
            textLabel.setVisible(false);
            flagLabel.setWrapText(true);
            flagLabel.setFont(Font.font(25));
            flagLabel.setVisible(false);
            border.setFill(Color.CHOCOLATE);
            border.setStroke(Color.BISQUE);
            this.setPrefWidth(30);
            this.setPrefHeight(30);
            this.getChildren().add(border);
            this.getChildren().add(textLabel);
            this.getChildren().add(flagLabel);
            this.isBomb = isBomb;
            this.setTranslateX(x * 30 + 2);
            this.setTranslateY(y * 30 - 2);
            this.setOnMouseClicked(event -> click(event.getButton()));
        }


        private void click(MouseButton mouseButton) {
            if(mouseButton.equals(MouseButton.PRIMARY)) {
                if (isClicked || isFlag) {
                    return;
                }
                isClicked=true;
                textLabel.setVisible(true);
                border.setFill(Color.AZURE);
                border.setStroke(Color.BISQUE);
                if (isBomb) {

                    for (int i=0; i<bombList.size();i++){
                        bombList.get(i).isClicked=true;
                        bombList.get(i).border.setFill(Color.RED);
                        bombList.get(i).border.setStroke(Color.DARKRED);
                        bombList.get(i).textLabel.setVisible(true);
                        bombList.get(i).flagLabel.setVisible(false);
                    }
                    sendAlert("Lost");
                }
                if (textLabel.getText().equals("")) {
                    for (Tile tile : getNeighbors(this)) {
                        tile.click(MouseButton.PRIMARY);
                    }
                }
            }
            if(mouseButton.equals(MouseButton.SECONDARY)) {
                flag();
            }
            checkWin();
        }


        private void flag(){
            if(!isClicked){
                if(!isFlag && numberOfFlags.get() >0){
                    flagLabel.setVisible(true);
                    isFlag=true;
                    numberOfFlags.set(numberOfFlags.get()-1);
                }
                else{
                    flagLabel.setVisible(false);
                    isFlag=false;
                    numberOfFlags.set(numberOfFlags.get()+1);
                }


            }
        }

        private void checkWin(){
            boolean flagsOK1=true;
            boolean flagsOK2=true;

            for(int i=0; i<nonBombList.size();i++){
                if(!nonBombList.get(i).isClicked){
                    flagsOK1=false;
                }

            }

            for(int i=0; i<bombList.size();i++){
                if(!bombList.get(i).isFlag){
                    flagsOK2=false;
                }
            }

            if(flagsOK1 || flagsOK2){
                sendAlert("WON");
            }


        }

        private void sendAlert(String result){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("You "+result+"!");
            alert.setHeaderText("You "+result+ " the game. ");
            alert.setContentText("Do you want to try again?");
            Optional<ButtonType> decision = alert.showAndWait();
            if (decision.get() == ButtonType.OK) {
                nonBombList.clear();
                bombList.clear();
                mainPane.setCenter(createField());
            } else {
                Platform.exit();
            }
        }

    }
}
