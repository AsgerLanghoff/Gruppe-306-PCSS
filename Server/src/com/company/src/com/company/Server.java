package src.com.company.src.com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Server extends Thread {
    private int port;
	private ArrayList<UserThread> users;
	private boolean lobby;
	private ServerSocket serverSocket;

	public Server(int port) {
		this.port = port;
		lobby = true;
		users = new ArrayList<>();
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Connection established. ");
		System.out.println("Enter Port: ");
		int paPort = scan.nextInt();
		Server server = new Server(paPort);
		scan.close();
		server.initiateServer();
	}

	public void initiateServer() {
		try {

			serverSocket = new ServerSocket(port);
			System.out.println("Server started at " + new Date());
			

			while (lobby) {
				Socket socket = serverSocket.accept();
				UserThread newUser = new UserThread(this, socket);
				users.add(newUser);
				newUser.start();
				System.out.println(users.size());
			}

		} catch (IOException e) {
			System.out.println("Unable to start server IOException");
			e.printStackTrace();
		}
	}

	public ArrayList<UserThread> getUsers() {
		return users;
	}

	public void removeUser(UserThread ut, String userName) {
		users.remove(ut);
		System.out.println(userName + " left the server");
	}

	public void sendToAll(String message, UserThread ut) {
		for (UserThread userThread : users) {
			if (userThread != ut)
				userThread.sendMessage(message);
		}
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}
}
