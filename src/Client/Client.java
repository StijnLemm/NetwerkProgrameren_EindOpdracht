package Client;

import java.net.*;
import java.io.*;

public class Client {

    public static void main(String[] args)throws IOException{
        Socket s = new Socket("localhost", 4999);

        PrintWriter pr = new PrintWriter(s.getOutputStream());
        pr.println("Hallo Stijn");
        pr.flush();

        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        String tekst = bf.readLine();
        System.out.println("Server : " + tekst);
    }
}
