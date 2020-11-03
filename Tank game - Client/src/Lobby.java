import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lobby{

    private List<SubLobby> subLobbies = new ArrayList<SubLobby>();
    public static List<String> players = new ArrayList<>();

    private String playerID;
    private boolean isHost;
    private String currentLobby;
    private LobbySender sender;

    Scanner scanner = new Scanner(System.in);


    Lobby() throws IOException { //initiates a connection to the lobby server and registers the player with a playerID


        System.out.println("Welcome to TANK the game!");



        System.out.println("What is your name?");
        String id = scanner.nextLine(); //letting the player make a playerID
        this.playerID = id;
        sender = new LobbySender(id); // initiating the sender
        sender.setPlayerID(id); // setting the playerID given by the user
        sender.sendPlayerID(); // sending the playerID to the server
        options();
    }

    public String PlayerID(){
        return playerID;
    }



    void createLobby(String code) throws IOException { // creates a new sub lobby on the server and adds the client to the server
        SubLobby subLobby = new SubLobby(code, this.playerID);
        this.subLobbies.add(subLobby);
        sender.sendSubLobby(subLobby);
    }


    void updateLobbies(List<SubLobby> s) throws IOException{ //get the lobbylist from the server and put it into the subLobbies list on the client
        for(int i = 0; i < s.size(); i++) {
            if(subLobbies.size() == 0){
                subLobbies.add(s.get(i));
                System.out.println("New lobby found!: "+s.get(i).getLobbyName());
                break;
            }
            for (int j = 0; j<subLobbies.size(); j++){
                if(s.get(i).getLobbyName().equals(subLobbies.get(j).getLobbyName())){
                    break; //Run next  i (atlså ikke add!!)
                } else if (j == subLobbies.size()-1){
                    subLobbies.add(s.get(i));
                    System.out.println("New lobby found!: "+s.get(i).getLobbyName());
                }
            }
        }
    }

    void joinLobby(String lobbyName) throws IOException { // adding the player to a lobby on the server.
        updateLobbies(sender.requestLobbyList());// updating the lobbylist 

        for (int i = 0; i < subLobbies.size(); i++) {
            if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                subLobbies.get(i).addToPlayers(this.playerID); //updating the chosen lobby by adding the client
            }
        }
        sender.updateLobby(this.playerID, lobbyName); // sending the player ID along with the lobby joined to the server to update their lobbylist
    }



    void readyCheck() throws IOException {
        sender.readyGame(currentLobby); // letting the server know that this player is ready to play
        updatePlayers(); // updating the player list
        System.out.println("READYY!!!!");
        startGame(this.currentLobby); //starting the game with the players on the given lobby

    }


    public void startGame(String lobby) throws IOException { // running the game with the data given from the current lobby

        this.players = sender.updatePlayers(this.currentLobby); //updating players in the current lobby
        sender.startGame(); 
        String[] arguments = new String[]{playerID};
        Game.main(arguments); // initiating a new main, launching the game
    }



    void removeFromLobby(String lobbyName) { // removing the player from a lobby.
        this.isHost = false;
        this.currentLobby = null;

        //finding the correct placement in the list of lobbies and players, to remove the specific playerID from the correct lobby.
        for (int i = 0; i < subLobbies.size(); i++) {
            if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                int j = subLobbies.get(i).getPlayers().indexOf(playerID);
                subLobbies.get(i).getPlayers().remove(j);
                if(subLobbies.get(i).getPlayers().size() == 0) {
                    subLobbies.remove(i);
                }
            }
        }
        // returning to the options page
        options();
    }



    void playerOptions(String lobbyName) { // giving the player all the different options from inside a lobby.
        System.out.println("Write \"list\" to see player list.");
        System.out.print("Write \"exit\" to leave lobby");
        System.out.println("Write \"ready\" if you are ready to play the game. The game starts when everyone is ready");
        try {
            String input = scanner.nextLine();

            if(input.equals("ready")){
                readyCheck(); //running the readyCheck function
            }


            if (input.equals("exit")) {
                removeFromLobby(lobbyName);
            } else if (input.equals("list")) {
                updatePlayers();

                for (int i = 0; i < subLobbies.size(); i++) {
                    if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                        subLobbies.get(i).printPlayers();
                    }
                }
                playerOptions(lobbyName);
            } else { // running if user input is none of the given options
                System.out.println("Unknown input, try again");
                playerOptions(lobbyName);
            }

        } catch (Exception e) {
            System.out.println("Unknown input, try again");
            hostOptions(lobbyName);
        }

    }

    void updatePlayers() throws IOException { // updating the list of players in the current lobby from the server to to client
        List<String> newPlayers = sender.updatePlayers(this.currentLobby);

        for(int i = 0; i < subLobbies.size(); i++) {
            if(subLobbies.get(i).getLobbyName().equals(this.currentLobby)){
                subLobbies.get(i).setPlayers(newPlayers);

            }
        }
    }

    void hostOptions(String lobbyName) {//does the same as the playerOptions, but just for the creator of the lobby
        System.out.println("Write \"ready\" if you are ready to start the game, or write \"list\" to see player list.");
        System.out.println("Write \"exit\" to leave lobby");

        try {
            String input = scanner.nextLine();
            if (input.equals("ready")) {
                readyCheck();
            } else if (input.equals("list")) {
                updatePlayers();
                for (int i = 0; i < subLobbies.size(); i++) {
                    if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                        subLobbies.get(i).printPlayers();
                    }
                }
                hostOptions(lobbyName);
            } else if (input.equals("exit")) {
                removeFromLobby(lobbyName);
            } else {
                System.out.println("Unknown input, try again");
                hostOptions(lobbyName);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Unknown input, try again");
            hostOptions(lobbyName);
        }

    }

    void options() { // giving the player all the options needed from the main screen, before joining or creating a lobby.
        System.out.println("Write \"create\" if you want to create a new game, or write \"join\" to see a list of available games");
        String option = scanner.nextLine();
        try {
            if (option.equals("create")) { // letting the player create a lobby and sending the lobby name to the server, while setting the creator as host
                System.out.println("Creating Lobby. What should the lobby be named?");
                String lobbyName = scanner.nextLine();
                createLobby(lobbyName);
                this.currentLobby = lobbyName;
                isHost = true;
            } else if (option.equals("join")) { // giving the user a list of the lobbies possible to join.
                updateLobbies(sender.requestLobbyList());

                if (subLobbies.size() == 0) {
                    System.out.println("No available games at the moment :(");// telling the user if there is no lobbies at the moment.
                    options(); // returning to options
                }

                System.out.println("Which lobby would you like to join? Here is a list of active games"); 
                for (int i = 0; i < subLobbies.size(); i++) {// listing the available lobbies
                    System.out.println(subLobbies.get(i).getLobbyName() + " (" + subLobbies.get(i).getPlayers().size() + "/4)");
                }
                String lobbyName = scanner.nextLine(); // letting the player enter the name of the lobby they want to join
                this.currentLobby = lobbyName; //setting this as the lobbyname on the client 
                isHost = false;

                joinLobby(lobbyName); //joining the lobby and telling the server.
            } else {
                System.out.println("Please enter a valid option");
                options(); // returning to the options main page, if the text submitted by the user is invalid.
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("please enter a valid option");
            options();
        }

        if (this.isHost) {
            hostOptions(this.currentLobby); // running the hostOptions if the user is the host of a lobby
        } else {
            playerOptions(this.currentLobby); // running the playerOptions if the user is not the host of a lobby.
        }
    }



    public static void main(String[] args) throws IOException { 
        Lobby l = new Lobby(); // running the lobby class.
    }
}