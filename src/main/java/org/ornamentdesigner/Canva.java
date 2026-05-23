package org.ornamentdesigner;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Canva {

    private Rectangle[][] rectangles = null;
    private Color[][] colors = null;

    private int cells;
    private int dimension;
    private int marginRectangle;
    private Color color;

    private boolean vertical = false;
    private boolean horizontal = false;

    public Canva(Group root, int cells, int dimension, int marginRectangle) {
        this.cells = cells;
        this.dimension = dimension;
        this.marginRectangle = marginRectangle;
        this.color = Color.RED;

        rectangles = new Rectangle[cells][cells];
        colors = new Color[cells][cells];

        var rectangle = new Rectangle(marginRectangle, marginRectangle, dimension, dimension);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);
        root.getChildren().add(rectangle);
        for (int i = 0; i < cells; i++) {
            double cord = marginRectangle + (i + 0.5) * dimension / cells;
            var horizontal = new Line(marginRectangle, cord, marginRectangle + dimension, cord);
            horizontal.setStroke(Color.GRAY);
            horizontal.setStrokeWidth(1);
            root.getChildren().add(horizontal);
            var vertical = new Line(cord, marginRectangle, cord, marginRectangle + dimension);
            vertical.setStroke(Color.LIGHTGRAY);
            vertical.setStrokeWidth(1);
            root.getChildren().add(vertical);

            for (int j = 0; j < cells; j++) {
                var cell = new Rectangle(
                        marginRectangle + i * dimension / cells,
                        marginRectangle + j * dimension / cells,
                        dimension / cells, dimension / cells
                );
                cell.setFill(Color.TRANSPARENT);
                rectangles[i][j] = cell;
                colors[i][j] = Color.TRANSPARENT;
                cell.setOnMousePressed(event -> onCellClick(event, cell));
                root.getChildren().add(cell);
            }
        }
    }

    public void clearCanvas() {
        for (int i = 0; i < cells; i++) {
            for (int j = 0; j < cells; j++) {
                colors[i][j] = Color.TRANSPARENT;
                rectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }
    }

    private void onCellClick(MouseEvent event, Rectangle cell) {
        int x = (int) ((cell.getX() - marginRectangle) / (dimension / cells));
        int y = (int) ((cell.getY() - marginRectangle) / (dimension / cells));
        if (event.isControlDown()) {
            colors[x][y] = Color.TRANSPARENT;
        } else {
            colors[x][y] = colors[x][y].equals(Color.TRANSPARENT) ? color : colors[x][y].equals(color) ? Color.TRANSPARENT : color;
        }
        cell.setFill(colors[x][y]);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }
}
