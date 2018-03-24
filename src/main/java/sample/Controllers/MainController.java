package sample.Controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MainController {

    @FXML
    BorderPane mainPane; //uploading panel from FXML


    private Tile[][] grid = new Tile[20][20];

    public MainController() {
        System.out.println("działa");
    }

    @FXML
    void initialize() {
        mainPane.setCenter(createField());
    }

    Pane createField() {
        Pane mainPanel = new Pane();
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {

                Tile tile = new Tile(x, y, Math.random() < 0.2);
                grid[x][y] = tile;
                mainPanel.getChildren().add(tile);

            }
        }
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {

                int howManyBombsAround = 0;
                List<Tile> neighbors = getNeighbors(grid[x][y]);
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!grid[x][y].textLabel.getText().equals("")) {
                        if (neighbors.get(i).textLabel.getText().equals(""))
                            neighbors.get(i).click();
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

            if (newX >= 0 && newX < 20
                    && newY >= 0 && newY < 20) {
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }


    private class Tile extends StackPane {
        private int x, y;
        private boolean isClicked = false;
        Label textLabel = new Label("");
        private boolean isBomb = false;
        Rectangle border = new Rectangle(30, 30);

        public Tile(int x, int y, boolean isBomb) {
            this.x = x;
            this.y = y;
            textLabel.setWrapText(true);
            textLabel.setFont(Font.font(25));
            textLabel.setVisible(false);
            border.setFill(Color.CHOCOLATE);
            border.setStroke(Color.BISQUE);
            this.setPrefWidth(30);
            this.setPrefHeight(30);
            this.getChildren().add(border);
            this.getChildren().add(textLabel);
            this.isBomb = isBomb;
            this.setTranslateX(x * 30 + 2);
            this.setTranslateY(y * 30 - 2);
            this.setOnMouseClicked(event -> click());
        }

        public void click() {
            if (isClicked) {
                return;
            }

            isClicked = true;
            textLabel.setVisible(true);
            border.setFill(Color.AZURE);
            border.setStroke(Color.BISQUE);
            if(isBomb){
                border.setFill(Color.RED);
                border.setStroke(Color.DARKRED);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("You lost!");
                alert.setHeaderText("You lost the game. ");
                alert.setContentText("Do you want to try again?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    mainPane.setCenter(createField());
                    return;
                } else {
                    Platform.exit();
                    return;
                }

            }
            if (textLabel.getText().equals("")) {
                getNeighbors(this).forEach(Tile::click);
            }
        }

        public void flag() {
            if (isClicked) {
                return;
            }
            this.textLabel.setText("F");
            this.textLabel.setVisible(true);
            return;
        }


    }
}
