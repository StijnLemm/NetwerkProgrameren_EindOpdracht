package Client;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public interface Button {
    void clear(GraphicsContext graphicsContext);
    void draw(GraphicsContext graphicsContext);
    boolean isSelected();
    void setSelected(boolean isSelected);
    boolean isHighlighted();
    void setHighlighted(boolean isHighlighted);
    boolean contains(Point2D point2D);
    Paint getColor();
}
