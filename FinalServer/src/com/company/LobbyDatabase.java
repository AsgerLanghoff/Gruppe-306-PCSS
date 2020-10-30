package com.company;

import java.util.List;

public class LobbyDatabase {

    //Variables
    String lobbyName;
    String host;

    //List of players
    List<String> players;
    int ready = 0;

    //LobbyDatabase constructor - assigns the host, lobby name and players to the variables
    LobbyDatabase(String lobbyName, String host, List<String> players) {
        this.host = host;
        this.lobbyName = lobbyName;
        this.players = players;
    }

    public void addPlayer(String player) {  //Adds the the specific string to the players
        this.players.add(player);
    }

    public String getHost() { //Gets the host
        return host; //Returns the host
    }

    public String getLobbyName() { //Get the lobby name
        return lobbyName; //Returns the lobby name
    }

    public int getReady() { //Gets the ready function
        return ready; //Returns the ready function
    }

    public void increaseReady() { //Increases the ready function
        ready++; //+1 to the ready function
    }


    public List<String> getPlayers() { //Gets the players
        return players; //Returns the players function
    }

}

