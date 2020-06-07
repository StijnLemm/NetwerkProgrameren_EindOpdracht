package Server;

import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import static javafx.application.Application.launch;

public class MultClientServer {

    private ServerGui serverGui;

    private CopyOnWriteArrayList<Socket> sockets;
    private int port;
    private boolean running;
    private ServerSocket serverSocket;

    public MultClientServer(int port, ServerGui serverGui) {
        this.port = port;
        this.serverGui = serverGui;
        this.running = false;
        this.sockets = new CopyOnWriteArrayList<>();
    }

    public void start(){
        try {
            running = true;
            serverSocket = new ServerSocket(port);
            serverGui.printLogLine("Server started!");

            new Thread(() -> {
                while (running){
                    try {
                        Socket socket = serverSocket.accept();
                        sockets.add(socket);
                        Platform.runLater(() -> {
                            serverGui.printLogLine("accepted socket: " + socket.getInetAddress());
                        });
                        if(sockets.size() >= 2){
                            new LinkThread(sockets.remove(0), sockets.remove(0));
                            Platform.runLater(() -> {
                            serverGui.printLogLine("successfully init LinkThread");
                            });
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        running = false;
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
        
    }

    public boolean isRunning() {
        return running;
    }
}
