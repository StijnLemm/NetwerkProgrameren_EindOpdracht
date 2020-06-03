package Client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class EraserButton extends Rectangle implements Button {

    private final int strokeWidth = 5;
    private boolean isSelected;
    private boolean isHighlighted;
    private final Image image;

    public EraserButton(String path, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.image = new Image(path);
        super.setFill(Color.WHITE);
        this.isHighlighted = false;
        this.isSelected = false;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean isHighlighted() {
        return isHighlighted;
    }

    @Override
    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    @Override
    public Paint getColor() {
        return super.getFill();
    }

    @Override
    public void clear(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(super.getX() - this.strokeWidth,
                super.getY() - this.strokeWidth,
                super.getWidth() + (2 * this.strokeWidth),
                super.getHeight() + (2 * this.strokeWidth));
    }

    public void draw(GraphicsContext graphicsContext) {
        if (isHighlighted) {
            graphicsContext.drawImage(this.image,
                    super.getX() - this.strokeWidth,
                    super.getY() - this.strokeWidth,
                    super.getWidth() + (2 * this.strokeWidth),
                    super.getHeight() + (2 * this.strokeWidth));
        } else {
            graphicsContext.drawImage(this.image, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        }
    }
}
