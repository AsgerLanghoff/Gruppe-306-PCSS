package com.company;

import java.util.*;

public class Database {

    private List<String> PlayerID = new ArrayList<>(); //Creates a ArrayList that contains the playerID's as strings
    private List<LobbyDatabase> lobbies = new ArrayList<>(); //Creates a ArrayList that contains the lobbies from the LobbyDatabase

    public void addPlayer(String player) { //Adds the playerID to the ArrayList
        PlayerID.add(player);
    }

    public void addLobby(String lobbyName, String host, List<String> players) { //Adds the lobbyname, host and the list of players to the lobbies ArrayList
        LobbyDatabase l = new LobbyDatabase(lobbyName, host, players);
        this.lobbies.add(l);    //Adds the lobby database to the lobby
    }

    public List<LobbyDatabase> getLobbies() { //Gets the lobbies
        return lobbies;  //Returns the lobbies
    }


}
