package Client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameContainer extends Application {

    private AtomicBoolean yourTurn;

    private Client client;

    private List<Button> buttons;
    private AnimationTimer animationTimer;
    private GraphicsContext graphicsContext;
    private Canvas canvas;
    private Stage window;
    private Pen pen;

    public GameContainer() {
        yourTurn = new AtomicBoolean();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.initWindow(primaryStage);
        this.initToolBar();

        try {
            this.client = new Client(6666, this);
            new Thread(client).start();
            System.out.println("client init success!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.initPen();
        this.initLoop();
    }

    private void initWindow(Stage window) {
        this.canvas = new Canvas();
        this.graphicsContext = canvas.getGraphicsContext2D();

        this.window = window;

        this.canvas.widthProperty().bind(this.window.widthProperty());
        this.canvas.heightProperty().bind(this.window.heightProperty());

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(this.canvas);

        this.window.setScene(new Scene(mainPane));
        this.window.show();
    }

    private void initToolBar() {

        this.graphicsContext.strokeLine(0, 100, this.canvas.getWidth(), 100);

        this.buttons = new ArrayList<>();

        this.buttons.add(new ColorCircle(50, 50, 40, Color.YELLOW));
        this.buttons.add(new ColorCircle(150, 50, 40, Color.RED));
        this.buttons.add(new ColorCircle(250, 50, 40, Color.BLUE));
        this.buttons.add(new ColorCircle(350, 50, 40, Color.BROWN));
        this.buttons.add(new ColorCircle(450, 50, 40, Color.GREEN));
        this.buttons.add(new ColorCircle(550, 50, 40, Color.BLACK));
        this.buttons.add(new EraserButton("eraser.png", 610, 10, 80, 80));

        this.graphicsContext.strokeOval(745, 45, 10, 10);

        this.graphicsContext.translate(810, 50);
        this.graphicsContext.scale(5, 5);
        this.graphicsContext.fillText("Word", 0, 0);
        this.graphicsContext.setTransform(new Affine());

        for (Button button : this.buttons) {
            button.draw(this.graphicsContext);
        }

        //this.setWord("Banaan");
    }

    private void initLoop() {
        canvas.setOnMouseDragged((event -> {
            if (yourTurn.get()) {
                if (event.getY() > 100) {
                    this.pen.update(event.getX(), event.getY());
                    this.pen.draw(graphicsContext);
                    this.client.setPen(this.pen);
                }
            }
        }));

        canvas.setOnMouseMoved((event -> {
            if (yourTurn.get()) {
                if (event.getY() < 100) {
                    for (Button button : this.buttons) {
                        if (button.contains(new Point2D(event.getX(), event.getY())) && !button.isSelected()) {
                            button.clear(this.graphicsContext);
                            button.setHighlighted(true);
                            button.draw(this.graphicsContext);
                        } else if (button.isHighlighted() && !button.isSelected()) {
                            button.clear(this.graphicsContext);
                            button.setHighlighted(false);
                            button.draw(this.graphicsContext);
                        }
                    }
                }
            }
        }));

        canvas.setOnMouseClicked((event -> {
            if (yourTurn.get()) {
                if (event.getY() < 100) {
                    for (Button button : this.buttons) {
                        if (button.contains(new Point2D(event.getX(), event.getY()))) {
                            button.clear(this.graphicsContext);
                            button.setSelected(true);
                            button.setHighlighted(false);
                            button.draw(this.graphicsContext);

                            this.pen.setColor((Color) button.getColor());

                            for (Button button1 : this.buttons) {
                                if (button1.isSelected() && button1 != button) {
                                    button1.clear(this.graphicsContext);
                                    button1.setSelected(false);
                                    button1.draw(this.graphicsContext);
                                }
                            }
                        }
                    }
                } else {
                    this.pen.update(event.getX(), event.getY());
                    this.pen.draw(this.graphicsContext);
                    this.client.setPen(this.pen);
                }
            }
        }));

        canvas.setOnScroll((event -> {
            if (yourTurn.get()) {

                this.graphicsContext.clearRect(700, 0, 96, 96);

                if (event.getDeltaY() < 40) {
                    System.out.println("scroll! down");
                    if (this.pen.getWidth() > 4) {
                        this.pen.setWidth(this.pen.getWidth() - 2);
                    }
                } else {
                    System.out.println("scroll! up");
                    if (this.pen.getWidth() < 90) {
                        this.pen.setWidth(this.pen.getWidth() + 2);
                    }
                }

                this.graphicsContext.strokeOval(750 - (this.pen.getWidth() / 2.0),
                        50 - (this.pen.getWidth() / 2.0),
                        this.pen.getWidth(),
                        this.pen.getWidth());
            }
        }));
    }

    private void initPen() {
        this.pen = new Pen(client);
    }

    public void drawPen(){
        this.pen.draw(this.graphicsContext);
    }

    private void clearToolbar() {
        this.graphicsContext.clearRect(0, 0, this.canvas.getWidth(), 100);
    }

    private void drawToolbar() {
        for (Button button : this.buttons) {
            button.draw(this.graphicsContext);
        }

        this.graphicsContext.strokeLine(0, 100, this.canvas.getWidth(), 100);
    }

    private void setWord(String word) {
        this.clearToolbar();
        this.drawToolbar();

        this.graphicsContext.translate(810, 50);
        this.graphicsContext.scale(5, 5);
        this.graphicsContext.fillText(word, 0, 0);
        this.graphicsContext.setTransform(new Affine());
    }

    public boolean isYourTurn() {
        return yourTurn.get();
    }

    public void setYourTurn(boolean yourTurn) {
        System.out.println("setting yourTurn: " + yourTurn);
        this.yourTurn.set(yourTurn);
    }

    public Pen getPen() {
        return pen;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }
}
