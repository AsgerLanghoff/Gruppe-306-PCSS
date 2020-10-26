package src.com.company.src.com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserThread extends Thread {
    
    private Server server;
	private Socket socket;
	private String playerID;
	private DataOutputStream output;
	private DataInputStream input;
    public static Database database = new Database();

	public UserThread(Server server, Socket socket) {
        this.server = server;
		this.socket = socket;
	
    }
    
	@Override
	public void run() {
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			playerID = input.readUTF();
			System.out.println(playerID + " joined the server");
			database.addPlayer(playerID);

			while(true){
				String commandType = input.readUTF();
				if(commandType.equals("sendSubLobby")){
					System.out.println(playerID + "creating lobby ");
					recieveSubLobby();
					System.out.println(playerID + "'s lobby created");
				}
				if(commandType.equals("requestLobbyList")){
					System.out.println("Sending lobby list to " + playerID);
					sendSubLobby();
				} 
				if(commandType.equals("readyGame")){
					System.out.println(playerID + "is Ready");
					readyGame();

				}



			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void recieveSubLobby() throws IOException {
		String lobbyName = input.readUTF();
		String host = input.readUTF();
		
		// int len = input.readInt();
		List<String> players = new ArrayList<>();
		players.add(host);
		database.addLobby(lobbyName, host, players);
		// for(int i = 0; i<len; i++){
		// 	players.add(input.readUTF());
		// }

	}

	public void sendSubLobby() throws IOException {
		List<LobbyDatabase> lobbies = new ArrayList<>();
		lobbies = database.getLobbies();
		output.writeInt(lobbies.size());
		System.out.println("lobby size: " + lobbies.size());
		for (int i = 0; i< lobbies.size(); i++){
			output.writeUTF(lobbies.get(i).getLobbyName());
			output.writeUTF(lobbies.get(i).getHost());
			output.writeInt(lobbies.get(i).getPlayers().size());
			for (int j = 0; j< lobbies.get(i).getPlayers().size(); j++){
				output.writeUTF(lobbies.get(i).getPlayers().get(j));
			}
		}
	}

	public void readyGame() throws IOException {
		String subLobbyInit = input.readUTF(); // recieves the name of the sublobby wanting to initialize
		recieveSubLobby();
		if (getLobbyName.equals(subLobbyInit)){

		}

	}


    public void sendMessage(String message) {
		try {
			output.writeUTF(message);
			output.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
    }
}