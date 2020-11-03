import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Game extends Application {
    DataInputStream input = LobbySender.getFromServer(); // initializes the input stream
    DataOutputStream output = LobbySender.getToServer(); // initializes the output stream


    //movement booleans
    boolean left = false;
    boolean right = false;
    boolean forward = false;
    boolean backward = false;


    private Pane root = new Pane(); //initializes a Pane called root

    //size of the window
    int width = 1200;
    int height = 700;

    int wallWidth = 10;

    //variables where the ID and index are saved when received from lobby
    String playerID;
    int playerIndex;

    int[] startX = {100, 100, 1100, 1100}; //start x-coordinates
    int[] startY = {100, 600, 100, 600}; //start y-coordinates


    List<Tank> tanks = new ArrayList<>(); //list of tanks filled in the Start function with the players from the lobby

    List<ClientReceiver> clientReceivers = new ArrayList<>();

    //defining the map layout
    private Map map_top = new Map(0, 0, 1200, wallWidth);
    private Map map_bot = new Map(0, height - wallWidth, 1200, wallWidth);
    private Map map_right = new Map(width - wallWidth, 0, wallWidth, height);
    private Map map_left = new Map(0, 0, wallWidth, height);



    private Map wall01 = new Map(360, 160, wallWidth, 160);
    private Map wall02 = new Map(160, 300,200, wallWidth);
    private Map wall03 = new Map(160,300,wallWidth,220);
    private Map wall04 = new Map(360,520,wallWidth,160);
    private Map wall05 = new Map(360,520,240,wallWidth);
    private Map wall06 = new Map(600,160,240,wallWidth);
    private Map wall07 = new Map(820,20,wallWidth,160);


    private Map wall08 = new Map(820,380,220,wallWidth);
    private Map wall09 = new Map(820,380,wallWidth,160);
    private Map wall10 = new Map(1020,160,wallWidth,240);

    // instantiating the map array
    Map[] maps = {map_top, map_bot, map_left, map_right, wall01, wall02, wall03, wall04, wall05, wall06, wall07, wall08, wall09, wall10};

    //previous coordinates that are updated every time the coordinates changes
    int prevX = 0;
    int prevY = 0;
    int prevA = 0;

    //variable for the frame rate
    public long lastUpdate = 0;


    private Parent createContent() { //creates the "draw" function - creates a Parent and returns it
        root.setPrefSize(width, height); //sets width and height of window

        for (int i = 0; i < tanks.size(); i++) { //puts the tanks from the tank-list and puts them in the frame
            if (tanks.get(i) != null) {
                root.getChildren().add(tanks.get(i));
            }
        }

        for (int i = 0; i < maps.length; i++) { //puts maps on in the frame
            root.getChildren().add(maps[i]);
        }


        AnimationTimer timer = new AnimationTimer() { //everything in this is called each frame


            @Override
            public void handle(long now) {
                sendToServer(); //sends x, y and angle to server everytime they change

                try {
                    int i = input.available(); //checks if there is any input from the server
                    System.out.println(i+"input available");
                    if (i != 0) { //if there is something - do something
                        int serverIndex = input.readInt(); //gets the index of the player we are receiving from
                        //if(serverIndex<=Lobby.players.size()) {
                        System.out.println(serverIndex+"index");
                        clientReceivers.get(serverIndex).clientUpdate(serverIndex, tanks, root);
                        // }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (now - lastUpdate >= 20_000_000) { //sets the frame rate


                    update();
                    lastUpdate = now;
                }
            }
        };
        timer.start(); //starts the animation timer
        return root; //returns the root
    }

    public void sendToServer() { //sends all the information (x-pos, y-pos and angle) to the server
        int X = (int) tanks.get(playerIndex).getTranslateX(); //my x-coordinates
        int Y = (int) tanks.get(playerIndex).getTranslateY(); //my y-coordinates
        int A = tanks.get(playerIndex).getAngle(); //my angle

        int[] positionInfo = {X, Y, A}; //put into array, because we tried to send an array which did not work

        if (positionInfo[0] != prevX || positionInfo[1] != prevY || positionInfo[2] != prevA) { //checks if anything has changed - we don't send anything if nothing changes
            try {
                output.writeUTF("INFO"); //sends INFO to the server, so
                output.writeInt(playerIndex);
                for (int i = 0; i < positionInfo.length; i++) {
                    output.writeInt(positionInfo[i]); //sends the array in a for loop

                    //updates the previous positions and angles
                    prevX = X;
                    prevY = Y;
                    prevA = A;
                }
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void update() {//function where everything that happens every frame is called
        for (int t = 0; t < tanks.size(); t++) {
            //moves ALL bullets on the map
            for (int i = 0; i < tanks.get(t).getProjectiles().length; i++) {
                if (tanks.get(t).getProjectiles()[i] != null) { //only does this function if there are bullets in the array
                    //for (int j = 0; j < maps.length; j++) {
                        tanks.get(t).getProjectiles()[i].moveBullet(maps);//moves bullets
                    //}

                    //removes a tank if hit
                    if (tanks.get(t).getProjectiles()[i].collision(tanks) != null) { //runs if a tank is hit
                        Tank tank = tanks.get(t).getProjectiles()[i].collision(tanks);
                        if (tank == tanks.get(playerIndex)) {// removes the specific tank hit and sends that message to server if the tank is beloning to this client.
                            if (tank.getDead() == false) {
                                root.getChildren().remove(tank);
                                try {
                                    output.writeUTF("DEAD");
                                    output.writeInt(playerIndex);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            tank.setDead();
                        }
                    }
                }
            }

            //checks the lifespan and removes bullet if it is over a threshold
            int threshold = 3000; //threshold - the bullets are removed after 300 frames
            for (int i = 0; i < tanks.get(t).getProjectiles().length; i++) {
                if (tanks.get(t).getProjectiles()[i] != null && tanks.get(t).getProjectiles()[i].getLifespan() >= threshold) { //only does this if there are bullets on the map and if one have been alive for 300 frames
                    root.getChildren().remove(tanks.get(t).getProjectiles()[i]); //removes the projectile child
                    tanks.get(t).getProjectiles()[i] = null; //removes projectile from array.
                }
            }
        }


        //moves if the boolean is true, this is smoother than having the move in the start function
        if (left) {
            tanks.get(playerIndex).rotateLeft(); // rotating left
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).rotateRight();

            }
        }
        if (right) {
            tanks.get(playerIndex).rotateRight(); //rotating right
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).rotateLeft();
            }
        }
        if (forward) {
            tanks.get(playerIndex).moveForward(); //moving forward
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).moveBackward();

            }
        }
        if (backward) {
            tanks.get(playerIndex).moveBackward(); //moving backwards
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).moveForward();

            }
        }
    }


    public void spawnProjectile(Tank tank) { // spawning a projectile and telling all other clients to do the same from this client's tank
        Projectile p = tank.shoot();
        if (p != null) {
            root.getChildren().add(p);
            if (tank.getPlayerID().equals(tanks.get(playerIndex).getPlayerID())) {
                try {
                    output.writeUTF("BULLET");
                    output.writeInt(playerIndex);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parameters para = getParameters(); //gets the parameters (playerID) when the lobby calls the game main
        List<String> list = para.getRaw();
        playerID = list.get(0);

        //initializing the tanks from the array from the lobby
        Color[] colors = {Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW}; //possible colors
        for (int i = 0; i < Lobby.players.size(); i++) {
            tanks.add(new Tank(startX[i], startY[i], 70, 40, Lobby.players.get(i), colors[i]));
            if (playerID.equals(Lobby.players.get(i))) {//check what playerID you got
                playerIndex = i; //saves what playerID you got
            }
        }

        for (int i = 0; i < Lobby.players.size(); i++) {
            clientReceivers.add(new ClientReceiver(root, this));
        }
        Scene scene = new Scene(createContent());

        //sets booleans to false if key is released
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT:
                    left = false;
                    break;
                case RIGHT:
                    right = false;
                    break;
                case UP:
                    forward = false;
                    break;
                case DOWN:
                    backward = false;
                    break;
            }
        });
        //sets booleans to true if key is pressed
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    left = true;
                    break;
                case RIGHT:
                    right = true;
                    break;
                case UP:
                    forward = true;
                    break;
                case DOWN:
                    backward = true;
                    break;
                case SPACE:
                    spawnProjectile(tanks.get(playerIndex));
                    break;
            }
        });
        stage.setScene(scene);//creates a stage using the scene that uses the root
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
