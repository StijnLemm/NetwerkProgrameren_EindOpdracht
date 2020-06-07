package Client;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client implements Runnable {

    private final CopyOnWriteArrayList<PenPackage> packages;
    private final GameContainer gameContainer;
    private final int port;
    private final String ip;

    private boolean running;

    private Socket s;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Client(int port, String ip, GameContainer gameContainer) {
        this.gameContainer = gameContainer;
        this.port = port;
        this.ip = ip;
        this.packages = new CopyOnWriteArrayList<PenPackage>();
    }

    @Override
    public void run() {
        try {
            this.s = new Socket(ip, port);

            Platform.runLater(gameContainer::start);

            objectOutputStream = new ObjectOutputStream((s.getOutputStream()));

            objectInputStream = new ObjectInputStream((s.getInputStream()));

            getRandomTurn();

            this.gameContainer.setupTimer();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Port niet aanwezig");
                alert.setContentText("Verbinding geweigerd.");
                alert.show();
            });
        }

        running = true;
        getInput().start();
        getOutput().start();
    }

    public void stop(boolean sendMsg) {
        this.running = false;
        try {
            this.s.close();
        } catch (IOException ignored) {};
    }

    public void sendPackage(Pen pen) {
        this.packages.add(new PenPackage(pen.getX(), pen.getY(), pen.getWidth(), pen.getColor()));
    }

    public void sendGuess(String guess) {
        String message = "GUESS:" + guess;
        try {
            this.objectOutputStream.writeObject(message);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        System.out.println("SENDING: " + msg);
        String message = "MSG:" + msg;
        try {
            this.objectOutputStream.writeObject(message);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getRandomTurn() throws IOException, ClassNotFoundException {

        int amountReceived;
        int random;

        do {
            random = new Random().nextInt(10);
            objectOutputStream.writeObject(String.valueOf(random));

            amountReceived = Integer.parseInt((String) objectInputStream.readObject());

            if (amountReceived < random) {
                Platform.runLater(() -> {
                    this.gameContainer.setYourTurn(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.gameContainer.setYourTurn(false);
                });
            }
        } while (random == amountReceived);
    }

    private Thread getOutput() {
        return new Thread(() -> {
            while (running) {
                try {
                    if (!packages.isEmpty()) {
                        objectOutputStream.writeObject(packages.remove(packages.size() - 1));
                        objectOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    gameContainer.onDisconnect();
                    return;
                }

            }
        });
    }

    private Thread getInput() {
        return new Thread(() -> {
            while (running) {
                try {
                    Object object = objectInputStream.readObject();
                    if (object instanceof PenPackage) {

                        PenPackage penPackage = (PenPackage) object;
                        Pen updatePen = gameContainer.getPen();

                        updatePen.update(penPackage.x, penPackage.y);
                        updatePen.setWidth(penPackage.width);
                        updatePen.setColor(penPackage.getColor());

                        Platform.runLater(gameContainer::drawPen);

                    } else if (object instanceof String) {
                        String message = (String) object;
                        if (message.contains("GUESS")) {

                            String guess = message.substring(6);

                            System.out.println(guess);

                            if (guess.toLowerCase().equals(gameContainer.getDrawWord().toLowerCase())) {
                                Platform.runLater(() -> {
                                    gameContainer.setYourTurn(false);
                                });
                                sendMsg("CORRECT");

                            } else {
                                sendMsg("WRONG");
                            }

                        } else if (message.contains("MSG")) {
                            if (message.contains("CORRECT")) {
                                Platform.runLater(() -> {
                                    gameContainer.setYourTurn(true);
                                });
                            } else if (message.contains("DISCONNECT")){
                                stop(false);
                                gameContainer.onDisconnect();
                            }
                        } else if (message.contains("SERVERSTOP")){
                            gameContainer.onServerStop();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    gameContainer.onDisconnect();
                    return;
                }
            }
        });
    }

    public String getIp() {
        return ip;
    }
}
