import java.util.Scanner;
import java.net.*;
import java.io.*;


public class Datasender {
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;
    int port = 8000;
    // ipv4 address for server
    String host = "192.168.43.236";
    boolean Continue = true;
    Socket socket = null;
    String playerID;

    Datasender(String playerID) {
        try {
            socket = new Socket(host, port);
            System.out.println("Connection to server established ");
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
            this.playerID = playerID;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void setID(String playerID){
        this.playerID = playerID;
    }

    void gameUpdate(){

    }

    void sendXpos(){

    }

}



