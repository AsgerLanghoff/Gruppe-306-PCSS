import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientReceiver extends Thread {
    private DataOutputStream output = LobbySender.getToServer();
    private DataInputStream input = LobbySender.getFromServer();

    Game game;
    Pane root;


    ClientReceiver(Pane root, Game game) throws IOException {
        this.game = game;
        this.root = root;
        start();
        run();
    }

    public void clientUpdate(int serverIndex, List<Tank> tanks, Pane root) throws IOException {
        String sendMessage = input.readUTF();
        if (sendMessage.equals("INFO")) {
            int x = input.readInt();
            tanks.get(serverIndex).setTranslateX(x);
            int y = input.readInt();
            tanks.get(serverIndex).setTranslateY(y);
            int a = input.readInt();
            tanks.get(serverIndex).setToAngle(a);
        }

        if (sendMessage.equals("BULLET")) {
            game.spawnProjectile(tanks.get(serverIndex));
        }

        if (sendMessage.equals("DEAD")) {
            System.out.println("serverdead");
            if (tanks.get(serverIndex).getDead() == false) {
                this.root.getChildren().remove(tanks.get(serverIndex));
            }
            tanks.get(serverIndex).setDead();
        }
    }
}
