package Client;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client implements Runnable {

    private boolean connected;
    private final int port;
    private boolean running;
    private CopyOnWriteArrayList<PenPackage> packages;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private GameContainer gameContainer;

    public Client(int port, String ip, GameContainer gameContainer) throws ClassNotFoundException, IOException {
        this.gameContainer = gameContainer;
        this.port = port;
        this.packages = new CopyOnWriteArrayList<PenPackage>();
        this.connected = false;
    }

    @Override
    public void run() {
        running = true;
        try {
            Socket s = new Socket("localhost", port);
            System.out.println("connected");

            this.connected = true;

            Platform.runLater(() -> {
                gameContainer.start();
            });

            objectOutputStream = new ObjectOutputStream((s.getOutputStream()));
            System.out.println("init outputStream");

            objectInputStream = new ObjectInputStream((s.getInputStream()));
            System.out.println("init inputStream");

            int amountReceived = 0;
            int random = 0;

            do {
                random = new Random().nextInt(10);
                objectOutputStream.writeObject(String.valueOf(random));
                System.out.println("Writing..: " + random);

                amountReceived = Integer.parseInt((String) objectInputStream.readObject());
                System.out.println("amount received..: " + amountReceived);

                if (amountReceived < random) {
                    Platform.runLater(() -> {
                        this.gameContainer.setYourTurn(true);
                    });
                    System.out.println("YOUR TURN!");
                }
            } while (random == amountReceived);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Port niet aanwezig");
                alert.setContentText("Verbinding geweigerd.");
                alert.show();
            });
        }

        input.start();
        output.start();
        System.out.println("Started threads");
    }

    private final Thread input = new Thread(new Runnable() {
        @Override
        public void run() {

            while (running) {
                try {
                    Object object = objectInputStream.readObject();
                    if (object instanceof PenPackage) {

                        PenPackage penPackage = (PenPackage) object;

                        Pen updatePen = gameContainer.getPen();

                        updatePen.update(penPackage.x, penPackage.y);
                        updatePen.setWidth(penPackage.width);
                        updatePen.setColor(penPackage.getColor());

                        Platform.runLater(() -> {
                            gameContainer.drawPen();
                        });

                    } else if (object instanceof String) {
                        String message = (String) object;
                        if (message.contains("GUESS")) {

                            String guess = message.substring(6);

                            System.out.println(guess);

                            if(guess.toLowerCase().equals(gameContainer.getDrawWord().toLowerCase())){
                                Platform.runLater(() -> {
                                    gameContainer.setYourTurn(true);
                                });
                                sendMsg("CORRECT");

                            } else {
                                sendMsg("WRONG");
                            }

                        } else if (message.contains("MSG")) {
                            if(message.contains("RECEIVED: CORRECT")){
                                System.out.println("CORRECT!");
                                Platform.runLater(() -> {
                                    gameContainer.setYourTurn(true);
                                });
                            }
                        }
                    }

                    Thread.sleep(5);

                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    });

    private final Thread output = new Thread(new Runnable() {
        @Override
        public void run() {
            while (running) {
                try {
                    if(gameContainer.isYourTurn()){

                        if(!packages.isEmpty()) {
                            objectOutputStream.writeObject(packages.remove(packages.size()-1));
                            objectOutputStream.flush();
                        }

                        Thread.sleep(5);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    });

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

    public void sendMsg(String msg){
        System.out.println("SENDING: " + msg);
        String message = "MSG:" + msg;
        try {
            this.objectOutputStream.writeObject(message);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void stop(){
        this.running = false;
    }
}
