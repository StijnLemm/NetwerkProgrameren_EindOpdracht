package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

    public boolean isRunning() {
        return running;
    }
}
