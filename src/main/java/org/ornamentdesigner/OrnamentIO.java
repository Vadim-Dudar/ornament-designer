package org.ornamentdesigner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class OrnamentIO {

    private static int cellSize = 10;
    private static final double animationStartDelayMs = 500;
    private static final double animationCellDelayMs = 80;

    public static void save(File file, Canva canva) {
        int cells = canva.getCells();
        int imageSize = cells * cellSize;

        BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < cells; i++) {
            for (int j = 0; j < cells; j++) {
                Color color = canva.getColor(i, j);

                int alpha = (int) Math.round(color.getOpacity() * 255);
                int red = (int) Math.round(color.getRed() * 255);
                int green = (int) Math.round(color.getGreen() * 255);
                int blue = (int) Math.round(color.getBlue() * 255);

                int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;

                for (int x = 0; x < cellSize; x++) {
                    for (int y = 0; y < cellSize; y++) {
                        int pixelX = cellSize * i + x;
                        int pixelY = cellSize * j + y;
                        image.setRGB(pixelX, pixelY, argb);
                    }
                }
            }
        }

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Canva load(File file, Group root, int rootDimension, int marginRectangle) {
        return load(file, root, rootDimension, marginRectangle, false);
    }

    public static Canva load(File file, Group root, int rootDimension, int marginRectangle, boolean animation) {
        try {
            return load(ImageIO.read(file), root, rootDimension, marginRectangle, animation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Canva load(InputStream inputStream, Group root, int rootDimension, int marginRectangle, boolean animation) {
        try {
            return load(ImageIO.read(inputStream), root, rootDimension, marginRectangle, animation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Canva load(BufferedImage image, Group root, int rootDimension, int marginRectangle, boolean animation) {
        Canva canva;

        int dimension = Math.min(image.getWidth(), image.getHeight());
        int cells = dimension / cellSize;
        canva = new Canva(root, cells, rootDimension, marginRectangle);
        Color[][] loadedColors = new Color[cells][cells];

        for (int i = 0; i < cells; i++) {
            for (int j = 0; j < cells; j++) {
                int pixelX = (int) ((i + 0.5) * cellSize);
                int pixelY = (int) ((j + 0.5) * cellSize);

                int argb = image.getRGB(pixelX, pixelY);

                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                loadedColors[i][j] = Color.rgb(red, green, blue, alpha / 255.0);

                if (!animation) {
                    canva.setColor(i, j, loadedColors[i][j]);
                }
            }
        }

        if (animation) {
            animateLoad(canva, loadedColors);
        }

        return canva;
    }

    private static void animateLoad(Canva canva, Color[][] loadedColors) {
        Timeline timeline = new Timeline();
        int cells = canva.getCells();

        for (int i = 0; i < cells; i++) {
            for (int j = 0; j < cells; j++) {
                int x = i;
                int y = j;
                timeline.getKeyFrames().add(new KeyFrame(
                        Duration.millis(animationStartDelayMs + (i + j) * animationCellDelayMs),
                        event -> canva.setColor(x, y, loadedColors[x][y])
                ));
            }
        }

        timeline.play();
    }

}
