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
    DataInputStream input = LobbySender.getFromServer();
    DataOutputStream output = LobbySender.getToServer();

    //Projectile[] projectiles; skal slettes


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

    int[] startX = {100, 100, 300, 300}; //start x-coordinates
    int[] startY = {100, 300, 100, 300}; //start y-coordinates


    List<Tank> tanks = new ArrayList<>(); //list of tanks filled in the Start function with the players from the lobby

    //map
    private Map top = new Map(0, 0, 1200, wallWidth);
    private Map bund = new Map(0, height - wallWidth, 1200, wallWidth);
    private Map hoejre = new Map(width - wallWidth, 0, wallWidth, height);
    private Map venstre = new Map(0, 0, wallWidth, height);
    private Map map1 = new Map(200, 100, width / 2, wallWidth);
    Map[] maps = {top, bund, venstre, hoejre, map1};


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
                if (now - lastUpdate >= 20_000_000) { //sets the frame rate
                    sendToServer();
                    receiveFromServer();
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

        /*try{
            if(tanks.get(playerIndex).getDead() == true) {
                System.out.println("dead");
                output.writeInt(playerIndex);
                output.writeUTF("DEAD");
            }
        }catch(IOException e){
            e.printStackTrace();
        }

         */

    }

    public void receiveFromServer() { //receives all the information from the server
        try {
            int i = input.available(); //checks if there is any input from the server
            if (i == 0) { //if there is nothing do nothing
            } else { //if there is something - do something
                String sendMessage = input.readUTF(); //saves a string from the server as sendMessage - it is either INFO or BULLET
                int serverIndex = input.readInt(); //gets the index of the player we are receiving from
                if (sendMessage.equals("INFO")) {
                    int x = input.readInt();
                    tanks.get(serverIndex).setTranslateX(x);
                    System.out.println("X: " + x);
                    int y = input.readInt();
                    tanks.get(serverIndex).setTranslateY(y);
                    System.out.println("Y: " + y);
                    int a = input.readInt();
                    tanks.get(serverIndex).setToAngle(a);
                    System.out.println("A: " + a);
                }
                if (sendMessage.equals("BULLET")) {
                    spawnProjectile(tanks.get(serverIndex));
                    System.out.println("BUELLELETETETET§!!!!!!!");
                }
                if(sendMessage.equals("DEAD")){
                    tanks.get(serverIndex).setDead();
                    System.out.println("serverdead");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void update() {//function where everything that happens every frame is called

        for (int t = 0; t < tanks.size(); t++) {
            //moves ALL bullets on the map
            for (int i = 0; i < tanks.get(t).getProjectiles().length; i++) {
                if (tanks.get(t).getProjectiles()[i] != null) { //only does this function if there are bullets in the array
                    for (int j = 0; j < maps.length; j++) {
                        tanks.get(t).getProjectiles()[i].moveBullet(maps[j]);//moves bullets
                    }

                    //removes a tank if hit
                    if (tanks.get(t).getProjectiles()[i].collision(tanks) != null) {//only does this if there is a hit tank

                        Tank tank = tanks.get(t).getProjectiles()[i].collision(tanks);
                        if (tank.getDead() == false) {
                            root.getChildren().remove(tank);
                            root.getChildren().remove(tanks.get(t).getProjectiles()[i]);//removes the bullet visually
                            tanks.get(t).getProjectiles()[i] = null;//removes the bullets from the array
                            //tanks.get(t).setDead();
                        }
                        tanks.get(t).setDead();
                        try{
                            if(tanks.get(playerIndex).getDead() == true) {
                                output.writeInt(playerIndex);
                                output.writeUTF("DEAD");
                                System.out.println("dead!!!!!!!!!!!!!!!!!!!!!!!!!");
                            }
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        //root.getChildren().remove(projectiles[i].collision(tanks));//removes the tank visually
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
            tanks.get(playerIndex).rotateLeft();
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).rotateRight();

            }
        }
        if (right) {
            tanks.get(playerIndex).rotateRight();
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).rotateLeft();
            }
        }
        if (forward) {
            tanks.get(playerIndex).moveForward();
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).moveBackward();

            }
        }
        if (backward) {
            tanks.get(playerIndex).moveBackward();
            if (tanks.get(playerIndex).isColliding(maps)) {
                tanks.get(playerIndex).moveForward();

            }
        }
    }


    public void spawnProjectile(Tank tank) {
        Projectile p = tank.shoot();
        if (p != null) {
            root.getChildren().add(p);
            if (tank.getPlayerID().equals(tanks.get(playerIndex).getPlayerID())) {
                try {
                    output.writeUTF("BULLET");
                    output.writeInt(playerIndex);
                    System.out.println("shoot");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        //creates a scene with the root createContent as input
        //projectiles = player.getProjectiles(); //initializes the projectile array
        Parameters para = getParameters(); //gets the parameters (playerID) when the lobby calls the game main
        System.out.println("para: " + para);
        List<String> list = para.getRaw();
        playerID = list.get(0);
        System.out.println("pID: " + playerID);

        //initializing the tanks from the array from the lobby
        Color[] colors = {Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW}; //possible colors
        for (int i = 0; i < Lobby.players.size(); i++) {
            tanks.add(new Tank(startX[i], startY[i], 70, 40, Lobby.players.get(i), colors[i]));
            if (playerID.equals(Lobby.players.get(i))) {//check what playerID you got
                playerIndex = i; //saves what playerID you got
                System.out.println("playerIndex: " + playerIndex);
            }
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
                    //spawnProjectile(tanks.get(playerIndex));
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
