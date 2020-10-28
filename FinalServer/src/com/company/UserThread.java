package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserThread extends Thread {

    private Server server;
    private Socket socket;
    private String playerID;
    boolean gameState = false;
    private DataOutputStream output;
    private DataInputStream input;
    public static Database database = new Database();

    public UserThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;

    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            playerID = input.readUTF();
            System.out.println(playerID + " joined the server");
            database.addPlayer(playerID);

            while(!gameState){
                String commandType = input.readUTF();
                if(commandType.equals("sendSubLobby")){  // creating a lobby on the server
                    System.out.println(playerID + "creating lobby ");
                    recieveSubLobby();
                    System.out.println(playerID + "'s lobby created");
                }
                if(commandType.equals("quit")){
                    server.sendToAll(playerID + " left the server ", this);
                    server.removeUser(this, playerID);
                    socket.close();
                }
                if(commandType.equals("requestLobbyList")){ //requesting the lobbylist
                    System.out.println("Sending lobby list to " + playerID);
                    sendSubLobby();
                }
                if(commandType.equals("updateLobby")){ // update the lobbylist on serverside
                    System.out.println("updating lobby ");
                    updateServerLobby();

                }
                if(commandType.equals("updatePlayers")){ // update the lobby/playerlist on clientside
                    System.out.println("updating playerlist");
                    updateClientLobby();
                }
                if(commandType.equals("playerReady")){
                    System.out.println(playerID + " is ready");
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

            while(gameState){
                String clientMessage = input.readUTF();

                if(clientMessage.equals("INFO")){
                    int x = input.readInt();
                    System.out.println("X: "+x);
                    int y = input.readInt();
                    System.out.println("Y: "+y);
                    int a = input.readInt();
                    System.out.println("A: "+a);
                    server.sendToAll("INFO", this);
                    server.sendToAllInts(x, this);
                    server.sendToAllInts(y, this);
                    server.sendToAllInts(a, this);
                }
                if(clientMessage.equals("BULLET")){
                    server.sendToAll("BULLET", this);
                }
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
