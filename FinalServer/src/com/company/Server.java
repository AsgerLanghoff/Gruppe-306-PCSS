package com.company;

//Imports

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Server extends Thread {

    //Variables
    private int port;
    private ArrayList<UserThread> users;
    private boolean lobby;
    private ServerSocket serverSocket; //Waits for requests to come in over the network

    //Server constructor
    public Server(int port) {
        this.port = port;
        lobby = true;
        users = new ArrayList<>();
    }

    public static void main(String[] args) { //The point from where the program starts its execution or simply the entry point of Java programs
        Scanner scan = new Scanner(System.in);
        System.out.println("Connection established. ");
        System.out.println("Enter Port: ");
        int paPort = scan.nextInt();
        Server server = new Server(paPort);
        scan.close();
        server.initiateServer();
    }

    public void initiateServer() {
        try {

            serverSocket = new ServerSocket(port); //Creates a server with the port
            System.out.println("Server started at " + new Date());


            while (lobby) { //Creates a while for the lobby
                Socket socket = serverSocket.accept(); //The server accepts the socket
                UserThread newUser = new UserThread(this, socket); //Creates a new thread
                users.add(newUser); //Creates a thread per user
                newUser.start(); //Starts the thread
                System.out.println(users.size());
            }

        } catch (IOException e) {
            System.out.println("Unable to start server IOException");
            e.printStackTrace();
        }
    }

    public void removeUser(UserThread ut, String userName) {
        users.remove(ut); //Removes a user from the server
        System.out.println(userName + " left the server");
    }

    public void sendToAll(String message, UserThread ut) {
        for (UserThread userThread : users) { //Sends a message
            if (userThread != ut) //If the userThread is not equal to the ut
                userThread.sendMessage(message); //Then is has to send a message
        }
    }

    public void sendToAllInts(int messageint, UserThread ut) {
        for (UserThread userThread : users) { //Sends a integer
            if (userThread != ut) { //If the userThread is not equal to the ut
                userThread.sendInt(messageint); //Then is has to send a integer
            }
        }

    }
}