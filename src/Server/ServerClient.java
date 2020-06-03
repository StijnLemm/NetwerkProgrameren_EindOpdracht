package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClient {

    private Thread c1Thread;
    private Thread c2Thread;
    private boolean socket1IsConnected;
    private boolean socket2IsConnected;
    private Socket socket1;
    private Socket socket2;
    private ServerSocket serverSocket1;
    private ServerSocket serverSocket2;
    private ObjectInputStream objectInputStream1;
    private ObjectOutputStream objectOutputStream1;
    private ObjectInputStream objectInputStream2;
    private ObjectOutputStream objectOutputStream2;

    public ServerClient(int port) {
        this.socket1IsConnected = false;
        this.socket2IsConnected = false;
        initConnection(port);
    }

    public void initConnection(int port){
        try {
            serverSocket1 = new ServerSocket(port);
            serverSocket2 = new ServerSocket(port + 1);
            System.out.println("Servers started");

            Thread socket1Thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket1 = serverSocket1.accept();
                        System.out.println("Connected: 1");

                        socket1IsConnected = true;
                        initServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread socket2Thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket2 = serverSocket2.accept();
                        System.out.println("Connected: 2");

                        socket2IsConnected = true;
                        initServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket1Thread.start();
            socket2Thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initServer(){

        if(!socket1IsConnected || !socket2IsConnected){
            return;
        }

        if (socket1 != null && socket2 != null) {
            try {
                objectInputStream1 = new ObjectInputStream(new BufferedInputStream(socket1.getInputStream()));
                objectOutputStream1 = new ObjectOutputStream(new BufferedOutputStream(socket1.getOutputStream()));
                objectInputStream2 = new ObjectInputStream(new BufferedInputStream(socket2.getInputStream()));
                objectOutputStream2 = new ObjectOutputStream(new BufferedOutputStream(socket2.getOutputStream()));
                System.out.println("Completed streams init");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        c1Thread = new Thread(() -> {
            while(true) {
                try {
                    objectOutputStream2.writeObject(objectInputStream1.readObject());
                    objectOutputStream2.flush();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();

                }
            }
        });

        c2Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        objectOutputStream1.writeObject(objectInputStream2.readObject());
                        objectOutputStream1.flush();
                    } catch (IOException | ClassNotFoundException e) {
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
