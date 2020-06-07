package Client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class ColorCircle extends Circle implements Button {

    private final int strokeWidth = 5;
    private boolean isHighlighted;
    private boolean isSelected;

    public ColorCircle(double centerX, double centerY, double radius, Paint fill) {
        super(centerX, centerY, radius, fill);
        this.isHighlighted = false;
        this.isSelected = false;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Paint getColor(){
        return super.getFill();
    }

    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(super.getFill());

        if (isHighlighted) {
            graphicsContext.fillOval(super.getCenterX() - super.getRadius() - this.strokeWidth,
                    super.getCenterY() - super.getRadius() - this.strokeWidth,
                    2 * super.getRadius() + 2 * this.strokeWidth,
                    2 * super.getRadius() + 2 * this.strokeWidth);
        } else {
            graphicsContext.fillOval(super.getCenterX() - super.getRadius(),
                    super.getCenterY() - super.getRadius(),
                    2 * super.getRadius(),
                    2 * super.getRadius());
        }
        graphicsContext.setFill(Color.BLACK);

        if(isSelected){
            graphicsContext.setLineWidth(this.strokeWidth);
            graphicsContext.strokeOval(super.getCenterX() - super.getRadius(),
                    super.getCenterY() - super.getRadius(),
                    2 * super.getRadius(),
                    2 * super.getRadius());
            graphicsContext.setLineWidth(1);
        }
    }

    public void clear(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(super.getCenterX() - super.getRadius() - this.strokeWidth,
                super.getCenterY() - super.getRadius() - this.strokeWidth,
                2 * super.getRadius() + 2 * this.strokeWidth,
                2 * super.getRadius() + 2 * this.strokeWidth);
    }
}
