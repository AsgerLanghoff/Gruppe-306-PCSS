package .idea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class UserThread extends Thread{
    private Server server;
    private Socket socket;
    private String userName;
    private DataOutputStream output;
    private DataInputStream input;


    public UserThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket
    }
}