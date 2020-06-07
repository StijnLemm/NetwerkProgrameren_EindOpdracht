package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class runs the server, its a multi client server because this server class makes it possible to log in for 2 people on the same port.
 * This class will also print information about what is going on with the server on the logfield in the ServerGUI.
 */

public class MultClientServer {


    private final List<LinkThread> linkThreads;
    private final List<Socket> sockets;
    private final ServerGui serverGui;
    private final int port;
    private boolean running;

    private ServerSocket serverSocket;

    public MultClientServer(int port, ServerGui serverGui) {
        this.port = port;
        this.serverGui = serverGui;
        this.running = false;
        this.linkThreads = new ArrayList<>();
        this.sockets = new ArrayList<>();
    }

    /**
     * This method starts the server and will also print information to the logfield of the ServerGUI
     */
    public void start() {
        try {
            running = true;
            serverSocket = new ServerSocket(port);
            serverGui.printLogLine("Server started!");

            new Thread(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        sockets.add(socket);
                        serverGui.printLogLine("accepted socket: " + socket.getInetAddress());
                        if (sockets.size() >= 2) {
                            linkThreads.add(new LinkThread(sockets.remove(0), sockets.remove(0)));
                            serverGui.printLogLine("successfully init LinkThread");
                        }

                    } catch (IOException e) {}
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will stop the server and will also print on the logfield of the ServerGUI when the server stopped.
     */
    public void stop() {

        this.running = false;

        for(LinkThread linkThread : linkThreads){
            linkThread.sendServerStop();
            linkThread.setRunning(false);
        }

        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.serverGui.printLogLine("Stopping server..");
    }

    /**
     * This method will return the value of th boolean running in true or false which can be used to stop Threads from looping.
     * @return
     */
    public boolean isRunning() {
        return running;
    }
}
