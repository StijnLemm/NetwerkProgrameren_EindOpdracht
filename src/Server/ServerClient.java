package Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClient {

    private ServerSocket serverSocket;
    private DataInputStream dataInputStream;
    private Socket socket;

    public ServerClient(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");
            socket = serverSocket.accept();
            System.out.println("Connected");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataInputStream = null;

        if (socket != null) {
            try {
                dataInputStream = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dataInputStream != null) {
            while (true) {
                try {
                    String line = dataInputStream.readUTF();
                    System.out.println(line);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ServerClient serverClient = new ServerClient(6666);
    }
}
