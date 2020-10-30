package com.company;

import java.util.*;

public class Database {

    private List<String>PlayerID = new ArrayList<>(); //Creates a ArrayList that contains the playerID's as strings
    private List<LobbyDatabase> lobbies = new ArrayList<>(); //Creates a ArrayList that contains the lobbies from the LobbyDatabase

    // public List<String> getPlayerList() { //Is never used
    //     return this.PlayerID;
    // }

    public void addPlayer(String player) { //Adds the playerID to the ArrayList
        PlayerID.add(player);
    }

    /*public void removePlayer(String player){ //Is never used
        PlayerID.remove(PlayerID.indexOf(player));
    }*/

    public void addLobby (String lobbyName, String host, List<String> players){ //Adds the lobbyname, host and the list of players to the lobbies ArrayList
        LobbyDatabase l = new LobbyDatabase(lobbyName,host,players);
        this.lobbies.add(l);
    }

    public List<LobbyDatabase> getLobbies() { //
        return lobbies;
    }



}
