package org.ornamentdesigner;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class OrnamentApplication extends Application {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int CELLS = 20;

    private final int marginRectangle = 50;
    private final int dimension = Math.min(WIDTH, HEIGHT) - 100;
    private final int sideBarWidth = WIDTH - dimension - marginRectangle * 3;

    private final Rectangle[][] rectangles = new Rectangle[CELLS][CELLS];
    private final Color[][] colors = new Color[CELLS][CELLS];
    private Color currentColor = Color.RED;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setResizable(false);
        stage.setTitle("Ornament Designer - Vadim Dudar");
        stage.getIcons().add(new Image(
                Objects.requireNonNull(OrnamentApplication.class.getResourceAsStream("icon.png"))
        ));
        stage.setScene(createHomeScene(stage));
        stage.show();
    }

    static void main() {
        launch();
    }

    private Scene createHomeScene(Stage stage) {
        var title = new Text("Ornament Designer");
        title.setFont(Font.font("Arial", 40));

        var description = new Text("Створи свій орнамент і переходь до полотна в один клік.");
        description.setFont(Font.font("Arial", 20));

        var startDrawingButton = new Button("Перейти до малювання");
        startDrawingButton.setFont(Font.font("Arial", 24));
        startDrawingButton.setPrefWidth(320);
        startDrawingButton.setPrefHeight(60);
        startDrawingButton.setOnAction(event -> stage.setScene(createEditorScene(stage)));

        var layout = new VBox(24, title, description, startDrawingButton);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, WIDTH, HEIGHT);
    }

    private Scene createEditorScene(Stage stage) {
        var root = new Group();

        createCanvas(root);

        var backBtn = new Button("На головну");
        backBtn.setLayoutX(marginRectangle * 2 + dimension);
        backBtn.setLayoutY(marginRectangle);
        backBtn.setFont(Font.font("Arial", 22));
        backBtn.setPrefWidth(sideBarWidth);
        backBtn.setOnAction(event -> stage.setScene(createHomeScene(stage)));
        root.getChildren().add(backBtn);

        var clearBtn = new Button("Clear");
        clearBtn.setLayoutX(marginRectangle * 2 + dimension);
        clearBtn.setLayoutY(marginRectangle * 2);
        clearBtn.setOnMouseClicked(event -> clearCanvas());
        clearBtn.setFont(Font.font("Arial", 22));
        clearBtn.setPrefWidth(sideBarWidth);
        root.getChildren().add(clearBtn);

        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setLayoutX(marginRectangle * 2 + dimension);
        colorPicker.setLayoutY(marginRectangle * 3);
        colorPicker.setOnAction(event -> currentColor = colorPicker.getValue());
        colorPicker.setPrefWidth(sideBarWidth);
        root.getChildren().add(colorPicker);

        return new Scene(root, WIDTH, HEIGHT);
    }

    private void clearCanvas() {
        for (int i = 0; i < CELLS; i++) {
            for (int j = 0; j < CELLS; j++) {
                colors[i][j] = Color.TRANSPARENT;
                rectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }
    }

    private void createCanvas(Group root) {
        var rectangle = new Rectangle(marginRectangle, marginRectangle, dimension, dimension);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);
        root.getChildren().add(rectangle);
        for (int i = 0; i < CELLS; i++) {
            double cord = marginRectangle + (i+0.5) * dimension / CELLS;
            var horizontal = new Line(marginRectangle, cord, marginRectangle + dimension, cord);
            horizontal.setStroke(Color.GRAY);
            horizontal.setStrokeWidth(1);
            root.getChildren().add(horizontal);
            var vertical = new Line(cord, marginRectangle, cord, marginRectangle + dimension);
            vertical.setStroke(Color.LIGHTGRAY);
            vertical.setStrokeWidth(1);
            root.getChildren().add(vertical);

            for (int j = 0; j < CELLS; j++) {
                var cell = new Rectangle(
                        marginRectangle + i * dimension / CELLS,
                        marginRectangle + j * dimension / CELLS,
                        dimension / CELLS, dimension / CELLS
                );
                cell.setFill(Color.TRANSPARENT);
                rectangles[i][j] = cell;
                colors[i][j] = Color.TRANSPARENT;
                cell.setOnMousePressed(event -> {
                    int x = (int) ((cell.getX() - marginRectangle) / (dimension / CELLS));
                    int y = (int) ((cell.getY() - marginRectangle) / (dimension / CELLS));
                    if (event.isControlDown()) {
                        colors[x][y] = Color.TRANSPARENT;
                    } else {
                        colors[x][y] = colors[x][y].equals(Color.TRANSPARENT) ? currentColor : colors[x][y].equals(currentColor) ? Color.TRANSPARENT : currentColor ;
                    }
                    cell.setFill(colors[x][y]);
                });
                root.getChildren().add(cell);
            }
        }
    }
}
