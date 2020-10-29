import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientTest {
public static DataOutputStream output;
public static DataInputStream input;


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        try {
            System.out.println("Type the IP or name of the server, if on the same machine type: localhost ");
            String ip = scan.nextLine();
            System.out.println("Type the port of the server, the default should be 8000 ");
            int port = Integer.parseInt(scan.nextLine());

            Socket socket = new Socket(ip, port);

            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());

            System.out.println("Successfully joined the server");
            System.out.print("Type your user name: ");

            output.writeUTF(scan.nextLine());
            output.flush();
            System.out.println("You can use commands 'ready' to initiate your game(warning: you can't chat anymore) or command 'quit' to leave the lobby");

            Thread write = new Thread(() -> {
                boolean connect = true;
                while (connect) {
                    try {
                        String message = scan.nextLine();
                        output.writeUTF(message);
                        output.flush();

                        if (message.equalsIgnoreCase("quit")) {
                            socket.close();
                            connect = false;
                            scan.close();
                        }

                        if (message.equalsIgnoreCase("ready")) {
                            String[] arguments = new String[]{};
                            Game.main(arguments);
                            connect = false;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            write.start();

            /*
            Thread read = new Thread(() -> {
                boolean connect = true;
                while (connect) {
                    try {
                        String message = input.readUTF();
                        System.out.println(message);


                        if (message.equalsIgnoreCase("Game started")) {
                            connect = false;
                        }
                    } catch (IOException e) {
                        System.out.println(e + " SocketException expected, do not worry");
                        break;
                    }
                }
                scan.close();

            });
            read.start();

             */

        } catch (
                IOException ex) {
            System.out.println(ex.toString() + '\n');
        }
    }


    public static DataInputStream getInput() {
        return input;
    }

    public static DataOutputStream getOutput() {
        return output;
    }

}


