package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClient {

    private Socket socket1;
    private Socket socket2;
    private ServerSocket serverSocket1;
    private ServerSocket serverSocket2;
    private DataInputStream dataInputStream1;
    private DataInputStream dataInputStream2;
    private DataOutputStream dataOutputStream1;
    private DataOutputStream dataOutputStream2;

    public ServerClient(int port) {
        initServer(port);
    }

    public void initServer(int port){
        try {
            serverSocket1 = new ServerSocket(port);
            serverSocket2 = new ServerSocket(port + 1);
            System.out.println("Servers started");
            socket1 = serverSocket1.accept();
            System.out.println("Connected: 1");
            socket2 = serverSocket2.accept();
            System.out.println("Connected: 2");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataInputStream1 = null;
        dataInputStream2 = null;

        if (socket1 != null && socket2 != null) {
            try {
                dataInputStream1 = new DataInputStream(new BufferedInputStream(socket1.getInputStream()));
                dataInputStream2 = new DataInputStream(new BufferedInputStream(socket2.getInputStream()));
                dataOutputStream1 = new DataOutputStream(new BufferedOutputStream(socket1.getOutputStream()));
                dataOutputStream2 = new DataOutputStream(new BufferedOutputStream(socket2.getOutputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Thread c1Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        String line = dataInputStream1.readUTF();
                        dataOutputStream2.writeUTF(line);
                        dataOutputStream2.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread c2Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        String line = dataInputStream2.readUTF();
                        dataOutputStream1.writeUTF(line);
                        dataOutputStream1.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        c1Thread.start();
        c2Thread.start();
    }

    public static void main(String[] args) {
        ServerClient serverClient = new ServerClient(6666);
    }
}
