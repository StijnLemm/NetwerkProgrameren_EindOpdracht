package Client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args)throws IOException{
        Scanner scanner = new Scanner(System.in);

        Socket s = new Socket("localhost", 6666);
        System.out.println("connected");
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        while(true){
            pr.write(scanner.nextLine());
            pr.flush();
        }
    }
}
