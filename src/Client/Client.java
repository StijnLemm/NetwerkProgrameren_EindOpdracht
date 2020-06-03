package Client;

import java.net.*;
import java.io.*;
import java.util.Random;

public class Client {

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private GameContainer gameContainer;

    public Client(int port, GameContainer gameContainer) throws IOException, ClassNotFoundException {
        this.gameContainer = gameContainer;

        Socket s = new Socket("localhost", port);
        System.out.println("connected");

        objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
        objectInputStream = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));

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

                        Pen pen = gameContainer.getPen();
                        Object penPackage = new PenPackage(pen.getX(), pen.getY(), pen.getWidth(), pen.getColor());
                        objectOutputStream.writeObject(penPackage);
                    } else {
                        Object object = objectInputStream.readObject();
                        if(object instanceof PenPackage){

                            PenPackage penPackage = (PenPackage) object;

                            gameContainer.getPen().update(penPackage.x, penPackage.y);
                            gameContainer.getPen().setWidth(penPackage.width);
                            gameContainer.getPen().setColor(penPackage.color);

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
