import java.util.ArrayList;
import java.util.List;

public class SubLobby {
    private String lobbyName;
    private List<String> players = new ArrayList<String>();
    private String host;


    SubLobby(String lobbyName, String host){
        this.lobbyName = lobbyName;
        addToPlayers(host);


    }


    void addToPlayers(String p) {
        this.players.add(p);

    }

    public String getLobbyName() {
        return lobbyName;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void printPlayers(){
        for (int i = 0; i < players.size(); i++) {
            System.out.println(" - "+players.get(i));
        }
    }

    public String getHost() {
        return host;
    }
}
