package Client;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameContainer {

    private AtomicBoolean yourTurn;

    private Client client;

    private String guessWord;

    private String drawWord;

    private Font impact;
    private BorderPane mainPane;
    private List<Button> buttons;
    private GraphicsContext graphicsContext;
    private Canvas canvas;
    private Stage window;
    private Pen pen;

    public GameContainer(Stage primaryStage) {
        this.window = primaryStage;
        this.initVar();
    }

    public void start(){
        this.initWindow();
        this.initStartScreen();

        this.setupTimer();
        this.initPen();
        this.initLoop();
    }

    public void initVar() {
        this.yourTurn = new AtomicBoolean();
        this.guessWord = "";
        this.drawWord = "";
        this.impact = new Font("Impact", 60);
    }

    private void initWindow() {
        this.canvas = new Canvas();
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.getGraphicsContext().setFont(impact);

        this.window.setResizable(false);

        this.canvas.widthProperty().bind(this.window.widthProperty());
        this.canvas.heightProperty().bind(this.window.heightProperty());

        this.mainPane = new BorderPane();
        this.mainPane.setCenter(this.canvas);

        this.window.setScene(new Scene(mainPane, 1200, 600));
        this.window.show();
    }

    private void initStartScreen(){

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

        this.drawWord = getRandomWord("/Client/Words.json");

        this.graphicsContext.fillText(drawWord, 810, 50);


        for (Button button : this.buttons) {
            button.draw(this.graphicsContext);
        }

        //this.setWord("Banaan");
    }

    private void initGuessBar() {
        this.graphicsContext.strokeLine(0, 100, this.canvas.getWidth(), 100);
    }

    private void initLoop() {

        System.out.println("INIT HANDLERS");

        canvas.setOnMouseDragged((event -> {
            if (yourTurn.get()) {
                if (event.getY() > 100) {
                    this.pen.update(event.getX(), event.getY());
                    this.pen.draw(graphicsContext);
                    this.client.sendPackage(this.pen);
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
                    this.client.sendPackage(this.pen);
                }
            }
        }));

        canvas.setOnScroll((event -> {
            if (yourTurn.get()) {

                this.graphicsContext.clearRect(700, 0, 96, 96);

                if (event.getDeltaY() < 40) {
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

        window.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            System.out.println(event.getText());
            if (!yourTurn.get()) {
                if (event.getCode() == KeyCode.BACK_SPACE && guessWord.length() != 0) {
                    guessWord = guessWord.substring(0, guessWord.length()-1);
                    updateGuess(guessWord);
                } else if (event.getCode() == KeyCode.ENTER && guessWord.length() > 0) {

                    this.client.sendGuess(guessWord);

                } else if (guessWord.length() < 15){
                    if(guessWord.length() > 0) {
                        guessWord = guessWord + event.getText();
                    } else {
                        guessWord = event.getText();
                    }
                    updateGuess(guessWord);
                }
            }
        });

        window.setOnCloseRequest((event -> {
            client.stop();
        }));
    }

    private void setupTimer(){
        this.graphicsContext.fillText("120", canvas.getWidth()- 100, 60);
    }

    private void initPen() {
        this.pen = new Pen();
    }

    private String getRandomWord(String jsonFileLocation){
        JsonReader reader = Json.createReader(getClass().getResourceAsStream(jsonFileLocation));
        JsonObject jsonObject = reader.readObject();
        JsonArray jsonValues = jsonObject.getJsonArray("words");
        return jsonValues.get(new Random().nextInt(jsonValues.size() - 1)).toString();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void drawPen() {
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

        this.graphicsContext.fillText(word, 810, 60);
    }

    private void updateGuess(String word) {
        this.graphicsContext.clearRect(0,0,canvas.getWidth() - 100, 90);
        Text guess = new Text(word);
        guess.setFont(impact);
        this.getGraphicsContext().fillText(word, (canvas.getWidth() / 2.0) - (guess.getLayoutBounds().getWidth() / 2.0), 80);
    }

    public boolean isYourTurn() {
        return yourTurn.get();
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn.set(yourTurn);

        this.clearScreen();

        if (yourTurn) {
            this.initToolBar();
        } else {
            this.initGuessBar();
        }
    }

    private void clearScreen(){
        this.graphicsContext.clearRect(0,0, this.canvas.getWidth(), this.canvas.getHeight());
    }

    public String getDrawWord() {
        return drawWord;
    }

    public Pen getPen() {
        return pen;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }
}
