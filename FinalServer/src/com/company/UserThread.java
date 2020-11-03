package com.company;

//Imports

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserThread extends Thread {

    //Variables
    private Server server;
    private Socket socket; //A socket is one endpoint of a two-way communication link between two programs running on the network
    private String playerID; //Gives the player its own name through a string
    boolean gameState = false;
    private DataOutputStream output; //DataOutputStream writes primitive Java data types to an output stream in a portable way
    private DataInputStream input; //DataInputStream reads primitive Java data types from an underlying input stream in a machine-independent way
    public static Database database = new Database();

    //UserThread constructor - assigns the server and socket to the variables
    public UserThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;

    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(socket.getInputStream()); //Returns an input stream for the given socket. If you close the returned InputStream, then close the linked socket
            output = new DataOutputStream(socket.getOutputStream()); //Returns an output stream for the given socket. If the close the returned OutputStream, then close the linked socket
            playerID = input.readUTF(); //readUTF decodes characters to a String
            System.out.println(playerID + " joined the server");
            database.addPlayer(playerID); //Adds the playerID to the database with playerIDs'

            while (!gameState) {
                String commandType = input.readUTF();
                if (commandType.equals("sendSubLobby")) { //Creating a lobby on the server
                    System.out.println(playerID + " creating lobby ");
                    recieveSubLobby();
                    System.out.println(playerID + " has created a lobby");
                }
                if (commandType.equals("quit")) {
                    server.sendToAll(playerID + " left the server ", this);
                    server.removeUser(this, playerID);
                    socket.close();
                }
                if (commandType.equals("requestLobbyList")) { //Requesting the lobby list
                    System.out.println("Sending lobby list to " + playerID);
                    sendSubLobby();
                }
                if (commandType.equals("updateLobby")) { //Update the lobby list on server side
                    System.out.println("updating lobby ");
                    updateServerLobby();

                }
                if (commandType.equals("updatePlayers")) { //Update the lobby/player list on client side
                    System.out.println("updating playerlist");
                    updateClientLobby();
                }
                if (commandType.equals("playerReady")) { //Recognizes that a client has sent a "playerReady" string. Then it checks if a player is ready.
                    System.out.println(playerID + " is ready"); //If this happens then increase the number of ready players in the lobby list
                    String lobby = input.readUTF();
                    for (int i = 0; i < database.getLobbies().size(); i++) {
                        if (database.getLobbies().get(i).getLobbyName().equals(lobby)) {
                            database.getLobbies().get(i).increaseReady();
                        }
                    }
                }
                if (commandType.equals("readyGame")) {

                    String lobby = input.readUTF();

                    for (int i = 0; i < database.getLobbies().size(); i++) { //Checks if the size of the ready lobby list on the server side has the same size as the getLobby list.
                        if (database.getLobbies().get(i).getLobbyName().equals(lobby)) { //Then it sends a true boolean from the server to clients that will stop the lobby while loop,
                            if (database.getLobbies().get(i).getReady() == database.getLobbies().get(i).getPlayers().size()) { //and then it starts the game while loop
                                output.writeBoolean(true);
                            } else {
                                output.writeBoolean(false); //If the boolean remains false then the lobby keeps running until the boolean becomes true
                            }
                        }
                    }

                }
                if (commandType.equals("startGame")) { //Ends the lobby while loop and starts the game while loop on the server side
                    gameState = true; //Boolean has become true
                }
            }

            //Prints update information about the game state when a tank moves, a bullet is generated and when a player is dead
            //This is done through the X-, Y- and A (angle of the direction that the tank points) coordinates of the tank
            //gameState is the game while loop. While this run, the game run
            while (gameState) {
                String clientMessage = input.readUTF();
                //Looks for either INFO, BULLET or DEAD in order to know what information that are going to arrive and processes that information
                if (clientMessage.equals("INFO")) {
                    int index = input.readInt(); //Reads the playerId from the clients
                    System.out.println("Index: " + index);
                    int x = input.readInt(); //Reads the X coordinate from the clients
                    System.out.println("X: " + x);
                    int y = input.readInt(); //Reads the Y coordinate from the clients
                    System.out.println("Y: " + y);
                    int a = input.readInt(); //Reads the A coordinate from the clients
                    System.out.println("A: " + a);
                    server.sendToAllInts(index, this); //Sends playerId
                    server.sendToAll("INFO", this); //Server get information from a client and sends that information to the others client
                    server.sendToAllInts(x, this); //Sends X position
                    server.sendToAllInts(y, this); //Sends Y position
                    server.sendToAllInts(a, this); //Sends A position

                }
                if (clientMessage.equals("BULLET")) {
                    int index = input.readInt(); //Reads playerID from the client
                    server.sendToAllInts(index, this); //Sends the playerID of the client
                    server.sendToAll("BULLET", this); //Sends the message "BULLET" to all other clients
                }
                if (clientMessage.equals("DEAD")) {
                    int index = input.readInt(); //Reads playerId from the client
                    server.sendToAllInts(index, this); //Sends the playerID of the client
                    server.sendToAll("DEAD", this); //Sends the message "DEAD" to all other clients

                }
                System.out.println(clientMessage); //Server debug print
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recieveSubLobby() throws IOException {
        String lobbyName = input.readUTF(); //The server reads a message from a client. The client has given a name to a subLobby that the server receives
        String host = input.readUTF(); //Reads which client that has hosted the subLobby

        List<String> players = new ArrayList<>(); //Creates a new list that can contain players
        players.add(host); //Adds the player that has create a subLobby to the list (the host)
        database.addLobby(lobbyName, host, players); //The subLobby gets added to the Database
    }

    public void updateClientLobby() throws IOException {
        String uLobbyName = input.readUTF(); //Reads the updated lobby name from the client
        System.out.println(uLobbyName); //Prints the lobby name
        for (int i = 0; i < database.getLobbies().size(); i++) { //Checks the lobby size
            if (database.getLobbies().get(i).getLobbyName().equals(uLobbyName)) { //Checks if the updated lobby size is equal to the original lobby size
                int len = database.getLobbies().get(i).getPlayers().size(); //Creates a new variable that contains the database and how many players that are in the lobby
                System.out.println(len); //Length of the player list
                output.writeInt(len); //Sends the length to all clients
                for (int j = 0; j < len; j++) {
                    output.writeUTF(database.getLobbies().get(i).getPlayers().get(j)); //Sends the playerID to the clients
                    System.out.println(database.getLobbies().get(i).getPlayers().get(j)); //Print the requested output to the clients
                }

            }
        }
    }

    public void updateServerLobby() throws IOException {
        String uLobbyName = input.readUTF(); //Reads the updated lobby name
        String uPlayerID = input.readUTF(); //Reads the updated playerID
        for (int i = 0; i < database.getLobbies().size(); i++) {
            if (database.getLobbies().get(i).getLobbyName().equals(uLobbyName)) { //If the lobby name is equal to the updated lobby name
                database.getLobbies().get(i).addPlayer(uPlayerID); //then assign the updated playerID's to the updated lobby
            }
        }
    }

    public void sendSubLobby() throws IOException {
        List<LobbyDatabase> lobbies = new ArrayList<>(); //Creates a new list of lobbies
        lobbies = database.getLobbies(); //Checks for lobbies in the database
        output.writeInt(lobbies.size()); //Sends the size of lobbies
        System.out.println("lobby size: " + lobbies.size()); //Prints the lobby size
        for (int i = 0; i < lobbies.size(); i++) {
            output.writeUTF(lobbies.get(i).getLobbyName()); //Sends the lobby name
            output.writeUTF(lobbies.get(i).getHost()); //Sends the host name
            output.writeInt(lobbies.get(i).getPlayers().size()); //Sends the amount of players in the lobby
            for (int j = 0; j < lobbies.get(i).getPlayers().size(); j++) {
                output.writeUTF(lobbies.get(i).getPlayers().get(j)); //Sends the playerID's to the clients
            }
        }
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message); //Sends a string
            output.flush(); //Flushes the stream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInt(int messageInt) {
        try {
            output.write(messageInt); //Sends a integer
            output.flush(); //Flushes the stream
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }
}