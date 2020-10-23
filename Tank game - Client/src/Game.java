import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Game extends Application {

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

    String playerID;

    private Tank player = new Tank(300, 300, 70, 40, playerID, Color.BLUE);
    private Tank player2 = new Tank(100, 100, 70, 40, "2", Color.BISQUE);
    Tank[] tanks = {player, player2};//puts the tanks into an array
    //private List<SubLobby> tank = new ArrayList<SubLobby>();


    //map
    private Map top = new Map(0, 0, 1200, wallWidth);
    private Map bund = new Map(0, height - wallWidth, 1200, wallWidth);
    private Map hoejre = new Map(width - wallWidth, 0, wallWidth, height);
    private Map venstre = new Map(0, 0, wallWidth, height);
    private Map map1 = new Map(200, 100, width / 2, wallWidth);
    Map[] maps = {top, bund, venstre, hoejre, map1};




    private Parent createContent() { //creates the "draw" function - creates a Parent and returns it
        root.setPrefSize(width, height); //sets width and height of window

        for(int i = 0; i< tanks.length; i++){ //makes the tanks
            if(tanks[i] != null) {
                root.getChildren().add(tanks[i]);
            }
        }

        for (int i = 0; i < maps.length; i++) {
            root.getChildren().add(maps[i]);
        }

        AnimationTimer timer = new AnimationTimer() { //everything in this is called each frame
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start(); //starts the animationtimer
        return root; //returns the root
    }

    public void update() {//function where everything that happens every frame is called
        //moves ALL bullets on the map
        for (int i = 0; i < projectiles.length; i++) {
            if (projectiles[i] != null) { //only does this function if there are bullets in the array
                for (int j = 0; j < maps.length; j++) {
                    projectiles[i].moveBullet(maps[j]);//moves bullets
                }

                //removes a tank if hit
                if (projectiles[i].collision(tanks) != null) {//only does this if there is a hit tank

                    Tank tank = projectiles[i].collision(tanks);
                    if (tank.getDead() == false) {
                        root.getChildren().remove(tank);
                        root.getChildren().remove(projectiles[i]);//removes the bullet visually
                        projectiles[i] = null;//removes the bullets from the array
                    }
                    tank.setDead();
                    //root.getChildren().remove(projectiles[i].collision(tanks));//removes the tank visually


                }
            }
        }

        //checks the lifespan and removes bullet if it is over a threshold
        int threshold = 3000; //threshold - the bullets are removed after 300 frames
        for (int i = 0; i < projectiles.length; i++) {
            if (projectiles[i] != null && projectiles[i].getLifespan() >= threshold) { //only does this if there are bullets on the map and if one have been alive for 300 frames
                root.getChildren().remove(projectiles[i]); //removes the projectile child
                projectiles[i] = null; //removes projectile from array.
            }
        }

        if (left) { //moves if the boolean is true, this is smoother than having the move in the start function
            player.rotateLeft();
            if (player.isColliding(maps)){
                player.rotateRight();

            }
        }
        if (right) {
            player.rotateRight();
            if (player.isColliding(maps)){
                player.rotateLeft();
            }
        }
        if (forward) {
            player.moveForward();
            if (player.isColliding(maps)){
                player.moveBackward();

            }
        }
        if (backward) {
            player.moveBackward();
            if (player.isColliding(maps)){
                player.moveForward();

            }
        }
    }


    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent()); //creates a scene with the root createContent as input
        projectiles = player.getProjectiles(); //initializes the projectile array
        Parameters para = getParameters();
        List<String> list = para.getRaw();
        playerID = list.get(0);
        System.out.println(list.get(0));


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
                    Projectile p = player.shoot();
                    projectiles = player.getProjectiles();
                    if (p != null) {
                        root.getChildren().add(p);
                    }
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
