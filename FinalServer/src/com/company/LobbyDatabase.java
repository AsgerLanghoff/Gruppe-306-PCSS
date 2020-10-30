package com.company;

import java.util.ArrayList;
import java.util.List;

public class LobbyDatabase {

    //Variables
    String lobbyName;
    String host;

    //List of players
    List<String> players;
    int ready = 0;

    //LobbyDatabase constructor - assigns the host, lobby name and players to the variables
    LobbyDatabase (String lobbyName, String host, List<String> players) {
        this.host = host;
        this.lobbyName = lobbyName;
        this.players = players;
    }

    public void addPlayer(String player) {
        this.players.add(player);
    }

    public String getHost() {
        return host;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getReady() {
        return ready;
    }

    public void increaseReady() {
        ready++;
    }


    public List<String> getPlayers() {
        return players;
    }

}

