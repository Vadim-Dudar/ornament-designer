package org.ornamentdesigner;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class OrnamentApplication extends Application {

    private static final int WIDTH = 1500;
    private static final int HEIGHT = 1000;
    private static final int CELLS = 20;

    private final Rectangle[][] rectangles = new Rectangle[CELLS][CELLS];
    private final Color[][] colors = new Color[CELLS][CELLS];
    private Color currentColor = Color.RED;

    @Override
    public void start(Stage stage) throws IOException {
        var root = new Group();

        createCanvas(root);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setResizable(false);
        stage.setTitle("Ornament Designer - Vadim Dudar");
        stage.getIcons().add(new Image(
                Objects.requireNonNull(OrnamentApplication.class.getResourceAsStream("icon.png"))
        ));
        stage.setScene(scene);
        stage.show();
    }

    static void main() {
        launch();
    }

    private void createCanvas(Group root) {
        int dimension = Math.min(WIDTH, HEIGHT) - 100;
        final int marginRectangle = 50;
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
                        colors[x][y] = colors[x][y].equals(Color.TRANSPARENT) ? currentColor : Color.TRANSPARENT;
                    }
                    cell.setFill(colors[x][y]);
                });
                root.getChildren().add(cell);
            }
        }
    }
}
