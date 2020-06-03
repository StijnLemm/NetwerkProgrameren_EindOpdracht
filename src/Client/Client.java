package Client;

import java.net.*;
import java.io.*;
import java.util.Random;

public class Client {

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private GameContainer gameContainer;

    public Client(int port, GameContainer gameContainer) throws IOException {
        this.gameContainer = gameContainer;

        Socket s = new Socket("localhost", port);
        System.out.println("connected");

        objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
        objectInputStream = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));

        int random =new Random().nextInt(10);
        objectOutputStream.write(random);

        this.gameContainer.setYourTurn(random > objectInputStream.read());

        input.start();
        output.start();
    }

    private final Thread input = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {

                    if(gameContainer.isYourTurn()){
                        Pen pen = gameContainer.getPen();

                        PenPackage penPackage = new PenPackage(gameContainer.getPen().)

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
