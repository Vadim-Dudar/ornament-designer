package org.ornamentdesigner;

import javafx.scene.Group;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OrnamentIO {

    private static int cellSize = 10;

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
        Canva canva;

        try {
            BufferedImage image = ImageIO.read(file);

            int dimension = Math.min(image.getWidth(), image.getHeight());
            int cells = dimension / cellSize;
            canva = new Canva(root, cells, rootDimension, marginRectangle);

            for (int i = 0; i < cells; i++) {
                for (int j = 0; j < cells; j++) {
                    int pixelX = (int) ((i+0.5) * cellSize);
                    int pixelY = (int) ((j+0.5) * cellSize);

                    int argb = image.getRGB(pixelX, pixelY);

                    int alpha = (argb >> 24) & 0xFF;
                    int red   = (argb >> 16) & 0xFF;
                    int green = (argb >> 8)  & 0xFF;
                    int blue  = argb & 0xFF;

                    Color color = Color.rgb(red, green, blue, alpha / 255.0);

                    canva.setColor(i, j, color);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return canva;
    }

}
