package Client;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class PenPackage implements Serializable {

    double x;
    double y;
    int width;
    String color;

    public PenPackage(double x, double y, int width, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.color = color.toString();
    }

    public Color getColor() {
        return Color.valueOf(color);
    }
}
