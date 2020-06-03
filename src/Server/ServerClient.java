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
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataInputStream = null;

        if(socket != null) {
            try {
                dataInputStream = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while(){

        }
    }

    public static void main(String[] args) {
        ServerClient serverClient = new ServerClient(66666);
    }
}
