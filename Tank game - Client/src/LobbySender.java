import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.*;
import java.io.*;

public class LobbySender {
    private static DataOutputStream toServer = null;
    private static DataInputStream fromServer = null;


    int port = 8000;
    String host;
    Socket socket = null;
    String playerID;
    Scanner scanner = new Scanner(System.in);


    LobbySender(String playerID) {
        initialize(playerID);
    }

    private void initialize(String PlayerID) { // establishing a connection to the server via their IPV4 adress and the correct port.
        try { 
            setIP(); // setting the ip adress to connect to
            socket = new Socket(host, port); // creating the socket to connect through
            //System.out.println("Connection to server established ");
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
            this.playerID = playerID; // setting the playerID
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Wrong IP, try again"); // printed if unable to connect
            initialize(PlayerID);// runs again to give the user a new attempt
        }

    }
    public void setIP() { // has the user enter the IPV4 adress to connect to
        System.out.println("What is the IP address?");
        String IP = scanner.nextLine();
        this.host = IP;
    }

    public void setPlayerID(String playerID){ // setting the playerID in the client
        this.playerID = playerID;
    }


    public void sendPlayerID() throws IOException { // setting the playerID in the server database
        toServer.writeUTF(playerID);
    }


    public void startGame() throws IOException { // starting the game state on ther server, initiating the game for all users if ready
        toServer.writeUTF("startGame");
    }

    public void readyGame(String subLobby) throws IOException { // telling the server that the client with this playerID in this lobby is ready
        toServer.writeUTF("playerReady");
        toServer.writeUTF(subLobby);
        boolean ready = false;
        while(!ready) {
            toServer.writeUTF("readyGame");
            toServer.writeUTF(subLobby);
            ready = fromServer.readBoolean(); // waiting for the ready boolean from the server. this is sent from the server when all players in the lobby are ready
        }
    }

    public void updateLobby(String playerID, String lobbyName) throws IOException { // sending this clients lobbyName and playerID to the server's lobbyDatabase
        toServer.writeUTF("updateLobby");
        toServer.writeUTF(lobbyName);
        toServer.writeUTF(playerID);
    }


    public void sendSubLobby(SubLobby lobby) throws IOException { // sending the information of a sublobby by the client hos to the server lobbyDatabase

        toServer.writeUTF("sendSubLobby");
        toServer.writeUTF(lobby.getLobbyName());
        toServer.writeUTF(lobby.getHost());

        System.out.println("HER SENDES INFO!");
    }


    public List<String> updatePlayers(String lobbyName) throws IOException { // recieving the list of players in a given lobby.
        List<String> players = new ArrayList<>(); //instansiating the list for the playerIDs to be put into
        toServer.writeUTF("updatePlayers");
        toServer.writeUTF(lobbyName);// telling the server what lobby the player list should come from
        int len = fromServer.readInt(); // the server telling the lobby, the amount of playerIDs about to be sent
        for(int i = 0; i<len; i++){ // getting each playerID in a for loop, while adding them to the list of players for the server.
            String newPlayer = fromServer.readUTF();
            players.add(newPlayer);
        }

        return players; // returning the new list of playerIDs
    }


    public List<SubLobby> requestLobbyList() throws IOException { // requests the list of lobbies from the server
        List<SubLobby> subLobbies = new ArrayList<SubLobby>(); // creating an array list to put the lobby names into
        toServer.writeUTF("requestLobbyList"); // asking the server for the lobby names

        int numOfLobbies = fromServer.readInt(); // getting the number of lobbies from the server
        System.out.println(numOfLobbies + " num of lobbies");
        for(int i = 0; i< numOfLobbies; i++) { // in a for loop, getting the list of lobbies, along with the number of players in each
            String lobbyName = fromServer.readUTF();
            String host = fromServer.readUTF();
            int len = fromServer.readInt(); // getting the number of lobbies
            List<String> players = new ArrayList<String>();
            for (int j = 0; j < len; j++) { 
                players.add(fromServer.readUTF()); // adding the players to each lobby
            }

            System.out.println("Received sublobby: "+lobbyName);
            SubLobby newLobby = new SubLobby(lobbyName, host);
            newLobby.setPlayers(players);
            subLobbies.add(newLobby); // adding the lobbies to the local lobby list on the client
        }
        System.out.println("Receiving done!");
        return subLobbies; // returning the list of lobbies available to join.
    }



    public static DataInputStream getFromServer() {
        return fromServer;
    }

    public static DataOutputStream getToServer() {
        return toServer;
    }
}






