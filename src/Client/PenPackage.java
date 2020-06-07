package Client;

import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * This class will update the width of the pen that is selected with the scroll wheel of the mouse.
 * It will also update the coordinates of the pen and the color that is selected by the user.
 * This information about the pen can only be changed and used by the person that is on turn to draw.
 */

public class PenPackage implements Serializable {

    final double x;
    final double y;
    final int width;
    final String color;

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
