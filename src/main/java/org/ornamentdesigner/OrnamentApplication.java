package org.ornamentdesigner;

import javafx.animation.PauseTransition;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class OrnamentApplication extends Application {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final String INTRO_IMAGE_RESOURCE = "icon.png";
    private static final double INTRO_DURATION_MS = 4000;
    private static final int MIN_CUSTOM_CELLS = 4;
    private static final int MAX_CUSTOM_CELLS = 64;
    private static final int DEFAULT_CUSTOM_CELLS = 12;

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
        stage.setScene(createIntroScene(stage));
        stage.show();
    }

    static void main() {
        launch();
    }

    private Scene createIntroScene(Stage stage) {
        var root = new Group();
        var canvasLayer = new Group();
        canvasLayer.setTranslateX((WIDTH - dimension - marginRectangle * 2) / 2.0);
        root.getChildren().add(canvasLayer);

        OrnamentIO.load(
                Objects.requireNonNull(OrnamentApplication.class.getResourceAsStream(INTRO_IMAGE_RESOURCE)),
                canvasLayer,
                dimension,
                marginRectangle,
                true
        );

        var introTimer = new PauseTransition(Duration.millis(INTRO_DURATION_MS));
        introTimer.setOnFinished(event -> stage.setScene(createHomeScene(stage)));
        introTimer.play();

        return new Scene(root, WIDTH, HEIGHT);
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
        RadioButton customSize = new RadioButton("Власний");
        customSize.setToggleGroup(cells);

        Spinner<Integer> customSizeSpinner = new Spinner<>(MIN_CUSTOM_CELLS, MAX_CUSTOM_CELLS, DEFAULT_CUSTOM_CELLS);
        customSizeSpinner.setEditable(true);
        customSizeSpinner.setPrefWidth(80);
        customSizeSpinner.disableProperty().bind(customSize.selectedProperty().not());

        var startDrawingButton = new Button("Перейти до малювання");
        startDrawingButton.setFont(Font.font("Arial", 24));
        startDrawingButton.setPrefWidth(320);
        startDrawingButton.setPrefHeight(60);
        startDrawingButton.setOnAction(event -> {
            int size = getSelectedCanvasSize(cells, customSizeSpinner);
            stage.setScene(createEditorScene(stage, size));
        });

        var openFileButton = new Button("Відкрити файл");
        openFileButton.setFont(Font.font("Arial", 18));
        openFileButton.setPrefWidth(220);
        openFileButton.setPrefHeight(44);
        openFileButton.setOnAction(event -> {
            File file = chooseFileToOpen(stage);
            if (file != null)
                stage.setScene(createEditorScene(stage, file));
        });

        HBox actions = new HBox(16, startDrawingButton, openFileButton);
        actions.setAlignment(Pos.CENTER);

        HBox radioBtn = new HBox(20, new Label("Розмірність"), size8, size12, size16, size20, size24, customSize, customSizeSpinner);
        radioBtn.setAlignment(Pos.CENTER);

        var layout = new VBox(24, title, description, actions, radioBtn);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, WIDTH, HEIGHT);
    }

    private Scene createEditorScene(Stage stage, int cells) {

        var root = new Group();

        canvas = new Canva(root, cells, dimension, marginRectangle);

        return createEditorLayout(stage, root);
    }

    private Scene createEditorScene(Stage stage, File file) {

        var root = new Group();

        canvas = OrnamentIO.load(file, root, dimension, marginRectangle);

        return createEditorLayout(stage, root);
    }

    private Scene createEditorLayout(Stage stage, Group root) {

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

        ColorPicker colorPicker = new ColorPicker(canvas.getCurrentColor());
        colorPicker.setLayoutX(marginRectangle * 2 + dimension);
        colorPicker.setLayoutY(marginRectangle * 3);
        colorPicker.setOnAction(event -> canvas.setCurrentColor(colorPicker.getValue()));
        colorPicker.setPrefWidth(sideBarWidth);
        root.getChildren().add(colorPicker);

        CheckBox horizontal = new CheckBox("Горизонтальна");
        horizontal.setFont(Font.font("Arial", 14));
        horizontal.setOnAction(event -> canvas.setHorizontal(horizontal.isSelected()));
        CheckBox vertical = new CheckBox("Вертикальна");
        vertical.setFont(Font.font("Arial", 14));
        vertical.setOnAction(event -> canvas.setVertical(vertical.isSelected()));
        var duplicateBtn = new Button("Дублювати");
        duplicateBtn.setFont(Font.font("Arial", 16));
        duplicateBtn.setPrefWidth(sideBarWidth);
        duplicateBtn.disableProperty().bind(horizontal.selectedProperty().not().and(vertical.selectedProperty().not()));
        duplicateBtn.setOnAction(event -> canvas.duplicateSymmetry());
        duplicateBtn.setLayoutX(marginRectangle * 2 + dimension);
        duplicateBtn.setLayoutY(marginRectangle * 6);
        root.getChildren().add(duplicateBtn);
        Label symmetryLabel = new Label("Режим симетрії");
        symmetryLabel.setFont(Font.font("Arial", 22));
        VBox symmetryLayout = new VBox(10, symmetryLabel, new HBox(24, horizontal, vertical));
        symmetryLayout.setLayoutX(dimension + marginRectangle * 3);
        symmetryLayout.setLayoutY(marginRectangle * 4);
        symmetryLayout.setAlignment(Pos.CENTER);
        root.getChildren().add(symmetryLayout);

        FileChooser fileChooser = new FileChooser();

        Menu fileMenu = new Menu("Файл");
        MenuItem saveItem = new MenuItem("Зберегти");
        saveItem.setOnAction(event -> {
            fileChooser.setTitle("Оберіть файл куди зберегти");
            fileChooser.setInitialFileName("icon.png");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null)
                OrnamentIO.save(file, canvas);
        });
        MenuItem loadItem = new MenuItem("Завантажити в програму");
        loadItem.setOnAction(event -> {
            File file = chooseFileToOpen(stage);
            if (file != null)
                stage.setScene(createEditorScene(stage, file));
        });
        fileMenu.getItems().addAll(saveItem, loadItem);

        MenuBar menuBar = new MenuBar(fileMenu);

        BorderPane layout = new BorderPane();
        layout.setCenter(root);
        layout.setTop(menuBar);

        return new Scene(layout, WIDTH, HEIGHT);
    }

    private int getSelectedCanvasSize(ToggleGroup cells, Spinner<Integer> customSizeSpinner) {
        RadioButton button = (RadioButton) cells.getSelectedToggle();
        if (!button.getText().equals("Власний")) {
            return Integer.parseInt(button.getText());
        }

        int size = customSizeSpinner.getValue();
        try {
            size = Integer.parseInt(customSizeSpinner.getEditor().getText());
        } catch (NumberFormatException ignored) {
        }

        size = Math.max(MIN_CUSTOM_CELLS, Math.min(MAX_CUSTOM_CELLS, size));
        customSizeSpinner.getValueFactory().setValue(size);
        return size;
    }

    private File chooseFileToOpen(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть файл для імпорту в програму");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        return fileChooser.showOpenDialog(stage);
    }
}
