package Client;

import sun.security.util.Pem;

import java.net.*;
import java.io.*;
import java.util.Random;

public class Client implements   Runnable {

    private final int port;

    private Pen pen;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private GameContainer gameContainer;

    public Client(int port, GameContainer gameContainer) throws ClassNotFoundException, IOException {
        this.gameContainer = gameContainer;
        this.port = port;
        this.pen = null;
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket("localhost", port);
            System.out.println("connected");

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
                    this.gameContainer.setYourTurn(true);
                    System.out.println("YOUR TURN!");
                }
            } while (random == amountReceived);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        input.start();
        output.start();
        System.out.println("Started threads");
    }

    private final Thread input = new Thread(new Runnable() {
        @Override
        public void run() {

            while (true) {
                try {

                    if(gameContainer.isYourTurn()){

                            if(pen != null) {
                                Object penPackage = new PenPackage(pen.getX(), pen.getY(), pen.getWidth(), pen.getColor());
                                objectOutputStream.writeObject(penPackage);
                            }

                            pen = null;

                    } else {
                        Object object = objectInputStream.readObject();
                        if(object instanceof PenPackage){

                            PenPackage penPackage = (PenPackage) object;

                            Pen updatePen = gameContainer.getPen();

                            updatePen.update(penPackage.x, penPackage.y);
                            updatePen.setWidth(penPackage.width);
                            updatePen.setColor(penPackage.getColor());

                            gameContainer.drawPen();

                        } else if (object instanceof String){

                        }
//                        if(line.contains("C")){
//                            line = line.substring(1);
//
//                            int x = 0;
//                            int y = 0;
//
//                            for (int i = 0; i < line.length(); i++) {
//                                if(line.charAt(i) == ':'){
//                                    x = Integer.parseInt(line.substring(0, i-1));
//                                    y = Integer.parseInt(line.substring(i + 1));
//                                }
//                            }
//
//                            gameContainer.getPen().update(x, y);
//                            gameContainer.getPen().draw(gameContainer.getGraphicsContext());
//                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public void setPen(Pen pen){
        this.pen = pen;
    }

    private final Thread output = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });
}
