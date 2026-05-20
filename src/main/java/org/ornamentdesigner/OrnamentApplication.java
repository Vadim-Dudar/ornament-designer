package org.ornamentdesigner;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    @Override
    public void start(Stage stage) throws IOException {
        var root = new Group();

        int dimension = Math.min(WIDTH, HEIGHT) - 100;
        var rectangle = new Rectangle(50, 50, dimension, dimension);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);
        root.getChildren().add(rectangle);
        for (int i = 0; i < CELLS; i++) {
            double x = 50 + (i+0.5) * dimension / CELLS;
            double y = 50 + (i+0.5) * dimension / CELLS;
            var horizontal = new Line(50, y, 50 + dimension, y);
            horizontal.setStroke(Color.GRAY);
            horizontal.setStrokeWidth(1);
            root.getChildren().add(horizontal);
            var vertical = new Line(x, 50, x, 50 + dimension);
            vertical.setStroke(Color.LIGHTGRAY);
            vertical.setStrokeWidth(1);
            root.getChildren().add(vertical);
        }

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
}
