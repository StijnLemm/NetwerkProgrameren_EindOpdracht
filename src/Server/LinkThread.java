package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LinkThread {

    private boolean running;

    private ObjectOutputStream objectOutputStream1;
    private ObjectInputStream objectInputStream1;
    private ObjectOutputStream objectOutputStream2;
    private ObjectInputStream objectInputStream2;

    public LinkThread(Socket socket1, Socket socket2) {

        this.running = true;

        try {
            objectInputStream1 = new ObjectInputStream(socket1.getInputStream());
            objectOutputStream1 = new ObjectOutputStream(socket1.getOutputStream());
            objectInputStream2 = new ObjectInputStream(socket2.getInputStream());
            objectOutputStream2 = new ObjectOutputStream(socket2.getOutputStream());
            System.out.println("Completed streams init");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {

                    if(socket1.isClosed() || socket2.isClosed()){
                        setRunning(false);
                        System.out.println("Closed socket");
                        return;
                    }

                    try {
                        objectOutputStream2.writeObject(objectInputStream1.readObject());
                        objectOutputStream2.flush();
                    } catch (IOException | ClassNotFoundException e) {
                        setRunning(false);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {

                    if(socket1.isClosed() || socket2.isClosed()){
                        setRunning(false);
                        System.out.println("Closed socket");
                        return;
                    }

                    try {
                        objectOutputStream1.writeObject(objectInputStream2.readObject());
                        objectOutputStream1.flush();
                    } catch (IOException | ClassNotFoundException e) {
                        setRunning(false);
                    }
                }
            }
        }).start();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
