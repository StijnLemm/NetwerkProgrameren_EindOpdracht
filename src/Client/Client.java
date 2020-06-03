package Client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args)throws IOException{
        Scanner scanner = new Scanner(System.in);

        Socket s = new Socket("localhost", 6667);
        System.out.println("connected");
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(s.getInputStream()));

        Thread input = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String line = dataInputStream.readUTF();
                        System.out.println(line);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread output = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        dataOutputStream.writeUTF(scanner.nextLine());
                        dataOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        input.start();
        output.start();
    }
}
