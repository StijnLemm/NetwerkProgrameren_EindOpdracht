package Client;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * Interface for the buttons on the screen, these methods will handle the drawing,
 * and the highlighting of the buttons.
 */

public interface Button {

    /**
     * clears the area of the button.
     * @param graphicsContext the context where the button is drawn.
     */
    void clear(GraphicsContext graphicsContext);

    /**
     * draws the button.
     * @param graphicsContext the context where the button is drawn.
     */
    void draw(GraphicsContext graphicsContext);

    /**
     * Set selected will enable a boolean, when the draw method is called the
     * button will appear with a black border. If so the button is selected.
     * @param isSelected true = with stroke, false = without stroke.
     */
    void setSelected(boolean isSelected);

    /**
     * Set highlighted will enable a boolean, when the draw method is called the
     * button will appear bigger. If so the button is highlighted.
     * @param isHighlighted true = bigger button, false = normal size.
     */
    void setHighlighted(boolean isHighlighted);

    Paint getColor();
    boolean isSelected();
    boolean isHighlighted();

    /**
     * This method will return true of false based on if the button area
     * contains the point.
     * @param point2D point to check.
     * @return true = area does contain point, false = area does not contain point.
     */
    boolean contains(Point2D point2D);
}
