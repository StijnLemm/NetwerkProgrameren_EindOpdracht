package Client;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client implements Runnable {

    private boolean replay;
    private boolean replayReceived;
    private boolean connected;
    private final int port;
    private boolean running;
    private String ip;

    private Socket s;

    private CopyOnWriteArrayList<PenPackage> packages;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private GameContainer gameContainer;

    public Client(int port, String ip, GameContainer gameContainer) throws ClassNotFoundException, IOException {
        this.gameContainer = gameContainer;
        this.port = port;
        this.ip = ip;
        this.packages = new CopyOnWriteArrayList<PenPackage>();
        this.connected = false;
        this.replay = false;
        this.replayReceived = false;
    }

    @Override
    public void run() {
        try {
            this.s = new Socket(ip, port);
            System.out.println("connected");

            this.connected = true;

            Platform.runLater(() -> {
                gameContainer.start();
            });

            objectOutputStream = new ObjectOutputStream((s.getOutputStream()));
            System.out.println("init outputStream");

            objectInputStream = new ObjectInputStream((s.getInputStream()));
            System.out.println("init inputStream");

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
        System.out.println("Started threads");
    }

//    private final Thread input = new Thread(new Runnable() {
//        @Override
//        public void run() {
//
//            while (running) {
//                System.out.println("input");
//                try {
//                    Object object = objectInputStream.readObject();
//                    if (object instanceof PenPackage) {
//
//                        PenPackage penPackage = (PenPackage) object;
//
//                        Pen updatePen = gameContainer.getPen();
//
//                        updatePen.update(penPackage.x, penPackage.y);
//                        updatePen.setWidth(penPackage.width);
//                        updatePen.setColor(penPackage.getColor());
//
//                        Platform.runLater(() -> {
//                            gameContainer.drawPen();
//                        });
//
//                    } else if (object instanceof String) {
//                        String message = (String) object;
//                        if (message.contains("GUESS")) {
//
//                            String guess = message.substring(6);
//
//                            System.out.println(guess);
//
//                            if(guess.toLowerCase().equals(gameContainer.getDrawWord().toLowerCase())){
//                                Platform.runLater(() -> {
//                                    gameContainer.setYourTurn(false);
//                                });
//                                sendMsg("CORRECT");
//
//                            } else {
//                                sendMsg("WRONG");
//                            }
//
//                        } else if (message.contains("MSG")) {
//                            System.out.println(message);
//                            if(message.contains("CORRECT")){
//                                System.out.println("RECEIVED: CORRECT!");
//                                Platform.runLater(() -> {
//                                    gameContainer.setYourTurn(true);
//                                });
//                            } else if (message.contains("REPLAY")) {
//
//                                System.out.println("replay boolean: " + replay);
//                                System.out.println("replayReceived boolean: " + replayReceived);
//
//                                if(replay){
//                                    gameContainer.restart();
//                                }
//                                replayReceived = true;
//                            }
//                        }
//                    }
//
//                    Thread.sleep(5);
//
//                } catch (IOException | ClassNotFoundException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    });

//    private final Thread output = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while (running) {
//                System.out.println("output");
//                try {
//                    if(gameContainer.isYourTurn()){
//
//                        if(!packages.isEmpty()) {
//                            objectOutputStream.writeObject(packages.remove(packages.size()-1));
//                            objectOutputStream.flush();
//                        }
//                        Thread.sleep(5);
//                    }
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    });

    public void getRandomTurn() throws IOException, ClassNotFoundException {

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
            } else {
                Platform.runLater(() -> {
                    this.gameContainer.setYourTurn(false);
                });
            }
        } while (random == amountReceived);
    }

    private Thread getOutput() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    private Thread getInput() {
        return new Thread(new Runnable() {
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

                                if (guess.toLowerCase().equals(gameContainer.getDrawWord().toLowerCase())) {
                                    Platform.runLater(() -> {
                                        gameContainer.setYourTurn(false);
                                    });
                                    sendMsg("CORRECT");

                                } else {
                                    sendMsg("WRONG");
                                }

                            } else if (message.contains("MSG")) {
                                System.out.println(message);
                                if (message.contains("CORRECT")) {
                                    System.out.println("RECEIVED: CORRECT!");
                                    Platform.runLater(() -> {
                                        gameContainer.setYourTurn(true);
                                    });
                                } else if (message.contains("DISCONNECT")){
                                    stop(false);
                                    gameContainer.onDisconnect();
                                }
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        gameContainer.onDisconnect();
                        return;
                    }
                }
            }
        });
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

    public String getIp() {
        return ip;
    }

    public void stop(boolean sendMsg) {
        this.running = false;
        if(sendMsg) sendMsg("DISCONNECT");
        try {
            this.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("STOP THREADS");
    }

    public void start() {
        System.out.println("START THREADS");
        running = true;
        getInput().start();
        getOutput().start();
    }
}
