import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientReceiver extends Thread { //client reciever is responsible for recieving all data during an active game.
    private DataOutputStream output = LobbySender.getToServer(); // establishing the output stream  
    private DataInputStream input = LobbySender.getFromServer(); // establishing the input stream

    Game game;
    Pane root;


    ClientReceiver(Pane root, Game game) throws IOException {
        this.game = game;
        this.root = root;
        start(); //initializing the game
        run();  //executing the runnable
    }

    public void clientUpdate(int serverIndex, List<Tank> tanks, Pane root) throws IOException { //recieving the placement and angle of all tanks on the server and putting it into the adhering tank on the client
        String sendMessage = input.readUTF(); // recieves the playerID
        if (sendMessage.equals("INFO")) { // recieves the placement data for the specific player ID
            int x = input.readInt();
            tanks.get(serverIndex).setTranslateX(x);
            int y = input.readInt();
            tanks.get(serverIndex).setTranslateY(y);
            int a = input.readInt();
            tanks.get(serverIndex).setToAngle(a);
        }

        if (sendMessage.equals("BULLET")) { //spawns a projectile from the tank sending the "BULLET" message
            game.spawnProjectile(tanks.get(serverIndex));
        }

        if (sendMessage.equals("DEAD")) { //removing the tank sending the "DEAD" message
            System.out.println("serverdead");
            if (tanks.get(serverIndex).getDead() == false) {
                this.root.getChildren().remove(tanks.get(serverIndex));
            }
            tanks.get(serverIndex).setDead();
        }
    }
}
