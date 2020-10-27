import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Game extends Application {
    DataInputStream input = ClientTest.getInput();
    DataOutputStream output = ClientTest.getOutput();


    Projectile[] projectiles;

    InputStream in = LobbySender.getFromServer();

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

    String playerID = "1";

    private Tank player = new Tank(300, 300, 70, 40, playerID, Color.BLUE);
    private Tank player2 = new Tank(100, 100, 70, 40, "2", Color.BISQUE);
    Tank[] tanks = {player, player2};//puts the tanks into an array


    //map
    private Map top = new Map(0, 0, 1200, wallWidth);
    private Map bund = new Map(0, height - wallWidth, 1200, wallWidth);
    private Map hoejre = new Map(width - wallWidth, 0, wallWidth, height);
    private Map venstre = new Map(0, 0, wallWidth, height);
    private Map map1 = new Map(200, 100, width / 2, wallWidth);
    Map[] maps = {top, bund, venstre, hoejre, map1};

    int prevX = 300;
    int prevY = 300;
    int prevA = 0;
    public long lastUpdate = 0;


    private Parent createContent() { //creates the "draw" function - creates a Parent and returns it
        root.setPrefSize(width, height); //sets width and height of window

        for (int i = 0; i < tanks.length; i++) { //makes the tanks
            if (tanks[i] != null) {
                root.getChildren().add(tanks[i]);
            }
        }

        for (int i = 0; i < maps.length; i++) {
            root.getChildren().add(maps[i]);
        }


        AnimationTimer timer = new AnimationTimer() { //everything in this is called each frame
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 20_000_000) {


                    int X = (int) player.getTranslateX();
                    int Y = (int) player.getTranslateY();
                    int A = player.getAngle();
                    int ID = 0;

                    int[] positionInfo = {X, Y, A};

                    if (positionInfo[0] != prevX || positionInfo[1] != prevY || positionInfo[2] != prevA) {
                        try {
                            output.writeUTF("INFO");
                            for (int i = 0; i < positionInfo.length; i++) {
                                output.writeInt(positionInfo[i]);
                                //System.out.println(positionInfo[i]);
                                prevX = X;
                                prevY = Y;
                                prevA = A;
                            }
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        int i = input.available();
                        if (i == 0) {
                        } else {
                            String sendMessage = input.readUTF();
                            if (sendMessage.equals("INFO")) {
                                int x = input.readInt();
                                player2.setTranslateX(x);
                                System.out.print("X: " + x);
                                int y = input.readInt();
                                player2.setTranslateY(y);
                                System.out.print("Y: " + y);
                                int a = input.readInt();
                                player2.setToAngle(a);
                                System.out.print("A: " + a);
                            }
                            if (sendMessage.equals("BULLET")) {
                                spawnProjectile(player2);
                                System.out.println("BUELLELETETETET§!!!!!!!");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }




            /*
                int[] array = new int[4];
                try {
                    for (int i = 0; i < 4; i++) {
                        array[i] = input.readInt();
                    }
                } catch (IOException e) {
                }

             */


                    update();

                    lastUpdate = now;
                }
            }
        };
        timer.start(); //starts the animationtimer
        return root; //returns the root
    }


    public void update() {//function where everything that happens every frame is called

        for (int t = 0; t < tanks.length; t++) {
            //moves ALL bullets on the map
            for (int i = 0; i < projectiles.length; i++) {
                if (tanks[t].getProjectiles()[i] != null) { //only does this function if there are bullets in the array
                    for (int j = 0; j < maps.length; j++) {
                        tanks[t].getProjectiles()[i].moveBullet(maps[j]);//moves bullets
                    }

                    //removes a tank if hit
                    if (tanks[t].getProjectiles()[i].collision(tanks) != null) {//only does this if there is a hit tank

                        Tank tank = tanks[t].getProjectiles()[i].collision(tanks);
                        if (tank.getDead() == false) {
                            root.getChildren().remove(tank);
                            root.getChildren().remove(tanks[t].getProjectiles()[i]);//removes the bullet visually
                            tanks[t].getProjectiles()[i] = null;//removes the bullets from the array
                        }
                        tank.setDead();
                        //root.getChildren().remove(projectiles[i].collision(tanks));//removes the tank visually


                    }
                }
            }

            //checks the lifespan and removes bullet if it is over a threshold
            int threshold = 3000; //threshold - the bullets are removed after 300 frames
            for (int i = 0; i < tanks[t].getProjectiles().length; i++) {
                if (tanks[t].getProjectiles()[i] != null && tanks[t].getProjectiles()[i].getLifespan() >= threshold) { //only does this if there are bullets on the map and if one have been alive for 300 frames
                    root.getChildren().remove(tanks[t].getProjectiles()[i]); //removes the projectile child
                    tanks[t].getProjectiles()[i] = null; //removes projectile from array.
                }
            }


        }

        if (left) { //moves if the boolean is true, this is smoother than having the move in the start function
            player.rotateLeft();
            if (player.isColliding(maps)) {
                player.rotateRight();

            }
        }
        if (right) {
            player.rotateRight();
            if (player.isColliding(maps)) {
                player.rotateLeft();
            }
        }
        if (forward) {
            player.moveForward();
            if (player.isColliding(maps)) {
                player.moveBackward();

            }
        }
        if (backward) {
            player.moveBackward();
            if (player.isColliding(maps)) {
                player.moveForward();

            }
        }
    }


    public void spawnProjectile(Tank tank) {
        Projectile p = tank.shoot();
        //projectiles = tank.getProjectiles();
        if (p != null) {
            root.getChildren().add(p);
            if (tank.getPlayerID().equals(player.getPlayerID())) {
                try {
                    output.writeUTF("BULLET");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent()); //creates a scene with the root createContent as input
        projectiles = player.getProjectiles(); //initializes the projectile array
        Parameters para = getParameters();
        List<String> list = para.getRaw();
        //playerID = list.get(0);
        playerID = "lol";
        //System.out.println(list.get(0));


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
                    spawnProjectile(player);
                   /* Projectile p = player.shoot();
                    projectiles = player.getProjectiles();
                    if (p != null) {
                        root.getChildren().add(p);
                        try {
                            output.writeUTF("BULLET");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    */

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
