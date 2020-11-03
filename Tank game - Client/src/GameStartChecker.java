import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Struct;

public class GameStartChecker extends Thread{
    private DataOutputStream toServer ;
    private DataInputStream fromServer ;

    @Override
    public void run() {// listening for the server to send a message to change from lobby to game state.
        super.run();
        while(true) {
            try {
                toServer = LobbySender.getToServer();
                fromServer = LobbySender.getFromServer();
                boolean runGame = false;

                while (runGame != true ) {
                    if(gameStart() == true){ // if the gamestart from the server is recieved as true, runGame is initiated.
                        runGame =true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    public boolean gameStart() throws IOException { // getting the gameStart boolean from server
         boolean b = fromServer.readBoolean();
            if (b){
             return true;
         }
        return false;
    }
}
