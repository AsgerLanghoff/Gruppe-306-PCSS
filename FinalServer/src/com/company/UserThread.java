package com.company;

//Imports
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserThread extends Thread {

    //Variables
    private Server server;
    private Socket socket; //A socket is one endpoint of a two-way communication link between two programs running on the network
    private String playerID; //Gives each person joining a list an index number that corresponds to their position in the list
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
            playerID = input.readUTF(); //The string of character is decoded from the UTF and returned as String.
            System.out.println(playerID + " joined the server");
            database.addPlayer(playerID);

            while(!gameState){
                String commandType = input.readUTF();
                if(commandType.equals("sendSubLobby")){ //Creating a lobby on the server
                    System.out.println(playerID + " creating lobby ");
                    recieveSubLobby();
                    System.out.println(playerID + " has created a lobby");
                }
                if(commandType.equals("quit")){
                    server.sendToAll(playerID + " left the server ", this);
                    server.removeUser(this, playerID);
                    socket.close();
                }
                if(commandType.equals("requestLobbyList")){ //Requesting the lobbylist
                    System.out.println("Sending lobby list to " + playerID);
                    sendSubLobby();
                }
                if(commandType.equals("updateLobby")){ //Update the lobbylist on serverside
                    System.out.println("updating lobby ");
                    updateServerLobby();

                }
                if(commandType.equals("updatePlayers")){ //Update the lobby/playerlist on clientside
                    System.out.println("updating playerlist");
                    updateClientLobby();
                }
                if(commandType.equals("playerReady")){ //If a player is ready, then the lobby recognizes that an additional index number (playerID)
                    System.out.println(playerID + " is ready");                                         //has been assigned to the particular lobby
                    String lobby = input.readUTF();
                    for ( int i = 0; i< database.getLobbies().size(); i++){
                        if( database.getLobbies().get(i).getLobbyName().equals(lobby)){
                            database.getLobbies().get(i).increaseReady();
                        }
                    }
                }
                if(commandType.equals("readyGame")){
                    String lobby = input.readUTF();

                    for ( int i = 0; i< database.getLobbies().size(); i++){
                        if( database.getLobbies().get(i).getLobbyName().equals(lobby)){
                            if(database.getLobbies().get(i).getReady()==database.getLobbies().get(i).getPlayers().size()){
                                output.writeBoolean(true);
                            }
                            else {
                                output.writeBoolean(false);
                            }
                        }
                    }

                }
                if(commandType.equals("startGame")){
                    gameState = true;
                }
            }

            //Print update information about the game state when a tank moves, a bullet is generated and when a player is dead
            //This is done through the X-, Y- and A (angle of the direction that the tank points) coordinates of the tank
            while(gameState){
                String clientMessage = input.readUTF();

                if(clientMessage.equals("INFO")){
                    int index = input.readInt();
                    System.out.println("Index: "+index);
                    int x = input.readInt();
                    System.out.println("X: "+x);
                    int y = input.readInt();
                    System.out.println("Y: "+y);
                    int a = input.readInt();
                    System.out.println("A: "+a);
                    server.sendToAll("INFO", this);
                    server.sendToAllInts(index, this);
                    server.sendToAllInts(x, this);
                    server.sendToAllInts(y, this);
                    server.sendToAllInts(a, this);

                }
                if(clientMessage.equals("BULLET")){
                    int index = input.readInt();
                    server.sendToAll("BULLET", this);
                    server.sendToAllInts(index, this);
                }
                if (clientMessage.equals("DEAD")){
                    int index = input.readInt();
                    server.sendToAll("DEAD", this);
                    server.sendToAllInts(index, this);

                }
                System.out.println(clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recieveSubLobby() throws IOException {
        String lobbyName = input.readUTF();
        String host = input.readUTF();

        // int len = input.readInt();
        List<String> players = new ArrayList<>();
        players.add(host);
        database.addLobby(lobbyName, host, players);
        // for(int i = 0; i<len; i++){
        // 	players.add(input.readUTF());
        // }

    }

    public void readyCheck() throws IOException{

    }

    public void updateClientLobby() throws IOException{
        String uLobbyName = input.readUTF();
        System.out.println(uLobbyName);// lobby navn
        for ( int i = 0; i< database.getLobbies().size(); i++){
            if( database.getLobbies().get(i).getLobbyName().equals(uLobbyName)){
                int len = database.getLobbies().get(i).getPlayers().size();
                System.out.println(len); // længde af playerlist
                output.writeInt(len);
                for(int j = 0; j< len; j++){
                    output.writeUTF(database.getLobbies().get(i).getPlayers().get(j));
                    System.out.println(database.getLobbies().get(i).getPlayers().get(j)); //playerliste
                }

            }
        }
    }

    public void updateServerLobby() throws IOException {
        String uLobbyName = input.readUTF();
        String uPlayerID = input.readUTF();
        for (int i = 0; i< database.getLobbies().size(); i++){
            if( database.getLobbies().get(i).getLobbyName().equals(uLobbyName)){
                database.getLobbies().get(i).addPlayer(uPlayerID);
            }
        }
    }

    public void sendSubLobby() throws IOException {
        List<LobbyDatabase> lobbies = new ArrayList<>();
        lobbies = database.getLobbies();
        output.writeInt(lobbies.size());
        System.out.println("lobby size: " + lobbies.size());
        for (int i = 0; i< lobbies.size(); i++){
            output.writeUTF(lobbies.get(i).getLobbyName());
            output.writeUTF(lobbies.get(i).getHost());
            output.writeInt(lobbies.get(i).getPlayers().size());
            for (int j = 0; j< lobbies.get(i).getPlayers().size(); j++){
                output.writeUTF(lobbies.get(i).getPlayers().get(j));
            }
        }
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInt(int messageInt){
        try {
            output.writeInt(messageInt);
            output.flush();
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }
}
