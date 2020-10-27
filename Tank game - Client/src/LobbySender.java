import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.*;
import java.io.*;

public class LobbySender {
    private static DataOutputStream toServer = null;
    private static DataInputStream fromServer = null;



    int port = 8000;
    // ipv4 address for server
    String host = "192.168.43.236";
    boolean Continue = true;
    Socket socket = null;
    String playerID;
    Scanner scanner = new Scanner(System.in);


    LobbySender(String playerID) {
        initialize(playerID);
    }

    private void initialize(String PlayerID) {
        try {
            setIP();
            socket = new Socket(host, port);
            //System.out.println("Connection to server established ");
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
            this.playerID = playerID;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Wrong IP, try again");
            initialize(PlayerID);
        }

    }
    public void setIP() {
        System.out.println("What is the IP address?");
        String IP = scanner.nextLine();
        this.host = IP;
    }

    public void setPlayerID(String playerID){
        this.playerID = playerID;
    }


    public void sendPlayerID() throws IOException {
        toServer.writeUTF(playerID);
    }

    public void recieveGameInfo(){


    }

    public void readyGame(String subLobby) throws IOException {
        toServer.writeUTF("playerReady");
        toServer.writeUTF(subLobby);
        boolean ready = false;
        while(!ready) {
            toServer.writeUTF("readyGame");
            toServer.writeUTF(subLobby);
            ready = fromServer.readBoolean();
        }
    }

    public void updateLobby(String playerID, String lobbyName) throws IOException {
        toServer.writeUTF("updateLobby");
        toServer.writeUTF(lobbyName);
        toServer.writeUTF(playerID);
    }


    public void sendSubLobby(SubLobby lobby) throws IOException {

        toServer.writeUTF("sendSubLobby");
        toServer.writeUTF(lobby.getLobbyName());
        toServer.writeUTF(lobby.getHost());

        System.out.println("HER SENDES INFO!");
        //int len = lobby.getPlayers().size();
        //toServer.writeInt(len);
        //for (int i = 0; i < len; i++){
            //toServer.writeUTF(lobby.getPlayers().get(i));
        //}
    }


    public List<String> updatePlayers(String lobbyName) throws IOException {
        List<String> players = new ArrayList<>();
        toServer.writeUTF("updatePlayers");
        toServer.writeUTF(lobbyName);
        int len = fromServer.readInt();

        for(int i = 0; i<len; i++){
            String newPlayer = fromServer.readUTF();
            players.add(newPlayer);
        }

        return players;
    }


    public List<SubLobby> requestLobbyList() throws IOException {
        List<SubLobby> subLobbies = new ArrayList<SubLobby>();
        toServer.writeUTF("requestLobbyList");

        int numOfLobbies = fromServer.readInt();
        System.out.println(numOfLobbies);
        for(int i = 0; i< numOfLobbies; i++) {
            String lobbyName = fromServer.readUTF();
            String host = fromServer.readUTF();
            int len = fromServer.readInt();
            List<String> players = new ArrayList<String>();
            for (int j = 0; j < len; j++) {
                players.add(fromServer.readUTF());
            }
            System.out.println("Recieved sublobby: "+lobbyName);
            SubLobby newLobby = new SubLobby(lobbyName, host);
            newLobby.setPlayers(players);
            subLobbies.add(newLobby);
        }
        System.out.println("recieving done!");
        return subLobbies;
    }



    public static DataInputStream getFromServer() {
        return fromServer;
    }

    public static DataOutputStream getToServer() {
        return toServer;
    }


}






