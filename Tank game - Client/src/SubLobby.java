import java.util.ArrayList;
import java.util.List;

public class SubLobby {
    private String lobbyName;
    private List<String> players = new ArrayList<String>();
    private String host;


    SubLobby(String lobbyName, String host){ // constructor
        this.lobbyName = lobbyName;
        this.host = host;
        addToPlayers(host);
    }


    void addToPlayers(String p) { // adding a player to the playerList
        this.players.add(p);
    }

    public void setPlayers(List<String> players){ // set the list of players to a specific list, for example one recieved from the server
        this.players = players;
    }

    public String getLobbyName() { // returning the lobbyname
        return lobbyName;
    }

    public List<String> getPlayers() { //returning the list of players
        return players;
    }

    public void printPlayers(){ // printing the list of players in the console.
        for (int i = 0; i < players.size(); i++) {
            System.out.println(" - "+players.get(i));
        }
    }

    public String getHost() {// returning the playerID of the host.
        return host;
    }
}
