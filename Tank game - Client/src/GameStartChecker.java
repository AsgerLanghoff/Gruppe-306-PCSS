import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Struct;

public class GameStartChecker extends Thread{
    private DataOutputStream toServer ;
    private DataInputStream fromServer ;

    @Override
    public void run() {
        super.run();
        while(true) {
            try {
                toServer = LobbySender.getToServer();
                fromServer = LobbySender.getFromServer();

                boolean runGame = false;
                while (runGame != true ) {
                    if(gameStart() == true){





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

    public boolean gameStart() throws IOException {
         boolean b = fromServer.readBoolean();
            if (b){
             return true;
         }
        return false;
    }
}
