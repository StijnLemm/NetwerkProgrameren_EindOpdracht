package Client;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameContainer {

    private final Stage window;
    private GraphicsContext graphicsContext;
    private Canvas canvas;

    private String guessWord;
    private String drawWord;

    private boolean yourTurn;
    private boolean started;
    private int points;

    private List<Button> buttons;

    private GameTimer gameTimer;
    private Client client;
    private Font impact;
    private Pen pen;

    public GameContainer(Stage primaryStage) {
        this.window = primaryStage;
        this.initVar();
    }

    public void start() {
        this.initWindow();
        this.initStartScreen();
        this.initPen();
        this.initHandlers();
    }

    public void initVar() {
        this.yourTurn = false;
        this.started = false;
        this.guessWord = "";
        this.drawWord = "";
        this.points = -1;
        this.impact = new Font("Impact", 60);
    }

    private void initWindow() {
        this.canvas = new Canvas();

        this.graphicsContext = canvas.getGraphicsContext2D();
        this.getGraphicsContext().setFont(impact);

        this.window.setResizable(false);

        this.canvas.widthProperty().bind(this.window.widthProperty());
        this.canvas.heightProperty().bind(this.window.heightProperty());

        BorderPane mainPane = new BorderPane();
        mainPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainPane.setCenter(this.canvas);

        this.window.setScene(new Scene(mainPane, 1200, 600));
        this.window.show();
    }

    private void initStartScreen() {
        this.updateGuess("Wachten op andere spelers!");
        makeButtonStartEnd("Opnieuw invoeren?", Color.GREEN);
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

        this.graphicsContext.fillText(drawWord, 810, 60);

        for (Button button : this.buttons) {
            button.draw(this.graphicsContext);
        }
    }

    private void initGuessBar() {
        this.guessWord = "";
        this.updateGuess("");
        this.graphicsContext.strokeLine(0, 100, this.canvas.getWidth(), 100);
    }

    private void initHandlers() {
        canvas.setOnMouseDragged((event -> {
            if (yourTurn) {
                if (event.getY() > 100) {
                    this.pen.update(event.getX(), event.getY());
                    this.pen.draw(graphicsContext);
                    this.client.sendPackage(this.pen);
                }
            }
        }));

        canvas.setOnMouseMoved((event -> {
            if (yourTurn) {
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
            if (yourTurn) {
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

            if (!started) {
                if(points == -1) {
                    client.stop(false);
                    StartScreen startScreen = new StartScreen();
                    startScreen.setVar(false, "");
                    startScreen.start(window);
                } else {
                    StartScreen startScreen = new StartScreen();
                    startScreen.setVar(true, this.client.getIp());
                    startScreen.start(window);
                }
            }
        }));

        canvas.setOnScroll((event -> {
            if (yourTurn) {

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
            if (!yourTurn && started) {
                if (event.getCode() == KeyCode.BACK_SPACE && guessWord.length() != 0) {
                    guessWord = guessWord.substring(0, guessWord.length() - 1);
                    updateGuess(guessWord);
                } else if (event.getCode() == KeyCode.ENTER && guessWord.length() > 0) {

                    this.client.sendGuess(guessWord);

                } else if (guessWord.length() < 15) {
                    if (guessWord.length() > 0) {
                        guessWord = guessWord + event.getText();
                    } else {
                        guessWord = event.getText();
                    }
                    updateGuess(guessWord);
                }
            }
        });

        window.setOnCloseRequest((event -> {
            client.stop(true);
        }));
    }

    private void initPen() {
        this.pen = new Pen();
    }

    public void setupTimer() {
        this.gameTimer = new GameTimer(this);
        new Thread(gameTimer).start();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;

        this.clearScreen();
        this.pen.setDefaults();

        if (yourTurn) {
            this.initToolBar();
        } else {
            this.initGuessBar();
        }

        this.points++;
        this.started = true;
    }

    public void onDisconnect() {
        this.points = -1;
        this.yourTurn = false;
        this.started = false;
        this.gameTimer.stop();
        this.clearScreen();
        updateGuess("Tegenstander heeft het spel verlaten!");
        makeButtonStartEnd("klik op het scherm om terug te gaan.", Color.BLUE);
    }

    public void onServerStop(){
        this.points = -1;
        this.yourTurn = false;
        this.started = false;
        this.gameTimer.stop();
        this.clearScreen();
        updateGuess("De server is gestopt!");
        makeButtonStartEnd("klik op het scherm om terug te gaan.", Color.BLUE);
    }

    public void updateTimer(int secondsLeft) {
        if(this.started) {
            this.graphicsContext.clearRect(canvas.getWidth() - 100, 0, 100, 90);
            this.graphicsContext.fillText(String.valueOf(secondsLeft), canvas.getWidth() - 100, 60);
        }
    }

    private void updateGuess(String word) {
        this.graphicsContext.clearRect(0, 0, canvas.getWidth() - 100, 90);
        Text guess = new Text(word);
        guess.setFont(impact);
        this.getGraphicsContext().fillText(word, (canvas.getWidth() / 2.0) - (guess.getLayoutBounds().getWidth() / 2.0), 80);
    }

    public void drawPen() {
        this.pen.draw(this.graphicsContext);
    }

    private void clearScreen() {
        this.graphicsContext.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
    }

    public void noMoreTimeLeft() {
        this.started = false;
        this.yourTurn = false;

        this.clearScreen();

        this.updateGuess("De tijd is over! Aantal punten: " + points);

        this.makeButtonStartEnd("Klik op het scherm om opnieuw te spelen", Color.GREEN);
    }

    private void makeButtonStartEnd(String msg, Color color) {
        Text text = new Text(msg);
        text.setFont(impact);
        this.graphicsContext.setFill(color);
        this.graphicsContext.fillRect(((this.canvas.getWidth() / 2.0) - ((text.getLayoutBounds().getWidth() / 2.0) + 20)),
                (this.canvas.getHeight() / 2.0) - 55,
                text.getLayoutBounds().getWidth() + 40,
                80);

        this.graphicsContext.setFill(Color.WHITE);
        this.graphicsContext.fillText(msg, (this.canvas.getWidth() / 2.0) - (text.getLayoutBounds().getWidth() / 2.0),
                (this.canvas.getHeight() / 2.0) + 10);
        this.graphicsContext.setFill(Color.BLACK);
        this.graphicsContext.strokeRect(((this.canvas.getWidth() / 2.0) - ((text.getLayoutBounds().getWidth() / 2.0) + 20)),
                (this.canvas.getHeight() / 2.0) - 55,
                text.getLayoutBounds().getWidth() + 40,
                80);
    }

    private String getRandomWord(String jsonFileLocation) {
        JsonReader reader = Json.createReader(getClass().getResourceAsStream(jsonFileLocation));
        JsonObject jsonObject = reader.readObject();
        JsonArray jsonValues = jsonObject.getJsonArray("words");
        return jsonValues.get(new Random().nextInt(jsonValues.size() - 1)).toString();
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
