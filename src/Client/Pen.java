package Client;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Pen {

    private double x;
    private double y;
    private int width;
    private Color color;
    private boolean isClearing;

    public Pen() {
        this.x = 0;
        this.y = 0;
        this.width = 10;
        this.color = Color.BLACK;
        this.isClearing = false;
    }

    public void setClearing(boolean clearing) {
        isClearing = clearing;
    }

    public boolean isClearing() {
        return isClearing;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void update(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setWidth(int amount){
        this.width = amount;
    }

    public int getWidth() {
        return width;
    }

    public void draw(GraphicsContext graphicsContext){
        graphicsContext.setFill(this.color);
        if(this.y > 100 + width/2.0) {
            graphicsContext.fillOval(x - width / 2.0, y - width / 2.0, width, width);
        }
        graphicsContext.setFill(Color.BLACK);
    }
}
