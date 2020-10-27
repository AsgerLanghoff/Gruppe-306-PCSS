package src.com.company.src.com.company;

import java.util.*;

public class Database {

    private List<String>PlayerID = new ArrayList<>();
    private List<LobbyDatabase> lobbies = new ArrayList<>();

    public List<String> getPlayerList() {
        return this.PlayerID;
    }

    public void addPlayer(String player) {
        PlayerID.add(player);
    }

    public void removePlayer(String player){
        PlayerID.remove(PlayerID.indexOf(player));
    }

    public void addLobby (String lobbyName, String host, List<String> players){
        LobbyDatabase l = new LobbyDatabase(lobbyName,host,players);
        this.lobbies.add(l);
    }

    public List<LobbyDatabase> getLobbies() {
        return lobbies;
    }



}
