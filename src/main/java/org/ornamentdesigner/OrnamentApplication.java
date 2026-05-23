package org.ornamentdesigner;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    private final int marginRectangle = 50;
    private final int dimension = Math.min(WIDTH, HEIGHT) - 100;
    private final int sideBarWidth = WIDTH - dimension - marginRectangle * 3;

    private Color currentColor = Color.RED;
    private Canva canvas;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setResizable(false);
        stage.setTitle("Ornament Designer - Vadim Dudar");
        stage.getIcons().add(new Image(Objects.requireNonNull(OrnamentApplication.class.getResourceAsStream("icon.png"))));
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

        ToggleGroup cells = new ToggleGroup();
        RadioButton size8 = new RadioButton("8");
        size8.setToggleGroup(cells);
        RadioButton size12 = new RadioButton("12");
        size12.setToggleGroup(cells);
        size12.setSelected(true);
        RadioButton size16 = new RadioButton("16");
        size16.setToggleGroup(cells);
        RadioButton size20 = new RadioButton("20");
        size20.setToggleGroup(cells);
        RadioButton size24 = new RadioButton("24");
        size24.setToggleGroup(cells);

        var startDrawingButton = new Button("Перейти до малювання");
        startDrawingButton.setFont(Font.font("Arial", 24));
        startDrawingButton.setPrefWidth(320);
        startDrawingButton.setPrefHeight(60);
        startDrawingButton.setOnAction(event -> {
            RadioButton button = (RadioButton) cells.getSelectedToggle();
            int size = Integer.parseInt(button.getText());
            stage.setScene(createEditorScene(stage, size));
        });

        HBox radioBtn = new HBox(20, new Label("Розмірність"), size8, size12, size16, size20, size24);
        radioBtn.setAlignment(Pos.CENTER);

        var layout = new VBox(24, title, description, startDrawingButton, radioBtn);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, WIDTH, HEIGHT);
    }

    private Scene createEditorScene(Stage stage, int cells) {

        var root = new Group();

        canvas = new Canva(root, cells, dimension, marginRectangle);

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
        clearBtn.setOnMouseClicked(event -> canvas.clearCanvas());
        clearBtn.setFont(Font.font("Arial", 22));
        clearBtn.setPrefWidth(sideBarWidth);
        root.getChildren().add(clearBtn);

        ColorPicker colorPicker = new ColorPicker(canvas.getColor());
        colorPicker.setLayoutX(marginRectangle * 2 + dimension);
        colorPicker.setLayoutY(marginRectangle * 3);
        colorPicker.setOnAction(event -> canvas.setColor(colorPicker.getValue()));
        colorPicker.setPrefWidth(sideBarWidth);
        root.getChildren().add(colorPicker);

        CheckBox horizontal = new CheckBox("Горизонтальна");
        horizontal.setFont(Font.font("Arial", 14));
        horizontal.setOnAction(event -> canvas.setHorizontal(horizontal.isSelected()));
        CheckBox vertical = new CheckBox("Вертикальна");
        vertical.setFont(Font.font("Arial", 14));
        vertical.setOnAction(event -> canvas.setVertical(vertical.isSelected()));
        Label symmetryLabel = new Label("Режим симетрії");
        symmetryLabel.setFont(Font.font("Arial", 22));
        VBox symmetryLayout = new VBox(10, symmetryLabel, new HBox(24, horizontal, vertical));
        symmetryLayout.setLayoutX(dimension + marginRectangle * 3);
        symmetryLayout.setLayoutY(marginRectangle * 4);
        symmetryLayout.setAlignment(Pos.CENTER);
        root.getChildren().add(symmetryLayout);

        Menu fileMenu = new Menu("Файл");
        MenuItem saveItem = new MenuItem("Зберегти");
        saveItem.setOnAction(event -> canvas.export());
        MenuItem loadItem = new MenuItem("Завантажити в програму");
        saveItem.setOnAction(event -> canvas.load());
        fileMenu.getItems().addAll(saveItem, loadItem);

        MenuBar menuBar = new MenuBar(fileMenu);

        BorderPane layout = new BorderPane();
        layout.setCenter(root);
        layout.setTop(menuBar);

        return new Scene(layout, WIDTH, HEIGHT);
    }
}
