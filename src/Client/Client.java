package Client;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
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

    /**
     * This method will init the socket to the server. If it succeeds, it will call
     * start in gameContainer
     */
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

    /**
     * This method will close the socket if needed. It will set the running boolean
     * to false to release all thread loops.
     * @param sendMsg true = sending disconnect msg, false = ignoring the send.
     */
    public void stop(boolean sendMsg) {
        this.running = false;
        if(sendMsg) sendMsg("DISCONNECT");
        try {
            this.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will save the pen objects attributes to send to the other client.
     * @param pen the current pen.
     */
    public void sendPackage(Pen pen) {
        this.packages.add(new PenPackage(pen.getX(), pen.getY(), pen.getWidth(), pen.getColor()));
    }

    /**
     * This method will send a guess message by writing a String with the tag 'GUESS'.
     * @param guess the guess to send.
     */
    public void sendGuess(String guess) {
        String message = "GUESS:" + guess;
        try {
            this.objectOutputStream.writeObject(message);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sends a message by writing a String with the tag 'MSG'.
     * @param msg the message to send.
     */
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

    /**
     * This method will send a random number from 0 to 10 to the other client, the other
     * client will do the same. Each of the clients will receive a number, the highest
     * number has the first turn. If the numbers are equal, the protocol will loop until
     * the numbers differ.
     * @throws IOException problems with sending or receiving from the stream.
     * @throws ClassNotFoundException receiving an object which class does not exist in the scope.
     */
    public void getRandomTurn() throws IOException, ClassNotFoundException {

        int amountReceived;
        int random;

        do {
            random = new Random().nextInt(10);
            objectOutputStream.writeObject(String.valueOf(random));

            amountReceived = Integer.parseInt((String) objectInputStream.readObject());

            if (amountReceived < random) {
                Platform.runLater(() -> this.gameContainer.setYourTurn(true));
            } else {
                Platform.runLater(() -> this.gameContainer.setYourTurn(false));
            }
        } while (random == amountReceived);
    }

    /**
     * This thread will handle the output from the socket.
     * @return returns a new thread.
     */
    private Thread getOutput() {
        return new Thread(() -> {
            while (running) {
                try {
                    if (!packages.isEmpty()) {
                        // writing objects from the queue, removes the last index.
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

    /**
     * This thread will handle the input from the socket.
     * @return returns a new thread.
     */
    private Thread getInput() {
        return new Thread(() -> {
            while (running) {
                try {
                    Object object = objectInputStream.readObject();
                    if (object instanceof PenPackage) { // object catching for pen updates.

                        PenPackage penPackage = (PenPackage) object;
                        Pen updatePen = gameContainer.getPen();

                        updatePen.update(penPackage.x, penPackage.y);
                        updatePen.setWidth(penPackage.width);
                        updatePen.setColor(penPackage.getColor());

                        Platform.runLater(gameContainer::drawPen);

                    } else if (object instanceof String) {

                        String message = (String) object;

                        if (message.contains("GUESS")) { //message catching for guesses.

                            String guess = message.substring(6);

                            if (guess.toLowerCase().equals(gameContainer.getDrawWord().toLowerCase())) {
                                Platform.runLater(() -> gameContainer.setYourTurn(false));
                                sendMsg("CORRECT");

                            } else {
                                sendMsg("WRONG");
                            }

                        } else if (message.contains("MSG")) { //message catching for info messages.
                            if (message.contains("CORRECT")) {
                                Platform.runLater(() -> gameContainer.setYourTurn(true));

                            } else if (message.contains("DISCONNECT")){
                                stop(false);
                                gameContainer.onDisconnect();
                            }
                        } else if (message.contains("SERVERSTOP")){ // server will return serverstop if the server stops.
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
