package Client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Objects;

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

    public void setDefaults(){
        this.width = 10;
        this.color = Color.BLACK;
        this.isClearing = false;
    }

    public int getWidth() {
        return width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void draw(GraphicsContext graphicsContext){

        graphicsContext.setFill(this.color);

        if(this.y > 100 + width/2.0) {
            graphicsContext.fillOval(x - width / 2.0, y - width / 2.0, width, width);
        }

        graphicsContext.setFill(Color.BLACK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, color, isClearing);
    }
}
