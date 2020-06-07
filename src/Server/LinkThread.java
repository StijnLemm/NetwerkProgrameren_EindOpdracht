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

    /**
     * This object will create a pipe between two sockets.
     * @param socket1 client number 1
     * @param socket2 client number 2
     */
    public LinkThread(Socket socket1, Socket socket2) {

        this.running = true;

        try {
            objectInputStream1 = new ObjectInputStream(socket1.getInputStream());
            objectOutputStream1 = new ObjectOutputStream(socket1.getOutputStream());
            objectInputStream2 = new ObjectInputStream(socket2.getInputStream());
            objectOutputStream2 = new ObjectOutputStream(socket2.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            while(running) {
                try {
                    // Socket 1 objects -> Socket 2
                    objectOutputStream2.writeObject(objectInputStream1.readObject());
                    objectOutputStream2.flush();
                } catch (IOException | ClassNotFoundException e) {
                    setRunning(false);
                }
            }
        }).start();

        new Thread(() -> {
            while(running) {
                try {
                    // Socket 2 objects -> Socket 1
                    objectOutputStream1.writeObject(objectInputStream2.readObject());
                    objectOutputStream1.flush();
                } catch (IOException | ClassNotFoundException e) {
                    setRunning(false);
                }
            }
        }).start();
    }

    /**
     * This method will send stop to the sockets to end the game.
     */
    public void sendServerStop(){
        try {
            objectOutputStream1.writeObject("SERVERSTOP");
            objectOutputStream2.writeObject("SERVERSTOP");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
