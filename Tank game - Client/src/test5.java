import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
/*
public class test5 extends Application {
    private Pane root = new Pane();
    int width = 1200;
    int height = 700;

    private Tank player = new Tank(300, 300, 70, 40, "player", Color.BLUE);


    private Parent createContent() {
        root.setPrefSize(width, height);
        root.getChildren().add(player);


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
        //nextLevel(); //initiater enemies - skal slette
        return root;
    }

 /*   private void nextLevel() { //laver de røde enemies - skal slettes senere
        for (int i = 0; i < 10; i++) {
            Sprite s = new Sprite(90 + i * 100, 150, 30, 30, "enemy", Color.RED);
            root.getChildren().add(s);
        }
    }

  */

 /*  private List<Projectile> projectiles() { //funktion der returner en liste
       System.out.println(root.getChildren().stream().map(n -> (Projectile) n).collect(Collectors.toList()));
        return root.getChildren().stream().map(n -> (Projectile) n).collect(Collectors.toList());
    }

  */

   /* private void update() {
        projectiles().forEach(p -> { //handles what happens to the sprites/bullets
            final int angle = player.getAngle();
            p.moveBullet();
                if (p.getBoundsInParent().intersects(player.getBoundsInParent())) {
                    player.dead = true;
                    p.dead = true;
                }
        });



        root.getChildren().removeIf(n -> {
            Projectile p = (Projectile) n;
            return p.dead;
        });
    }

    private void shoot(Tank who) { //shoot function, creates sprites from shooting player
        int bulletW = 5; //bullet width
        int bulletH = 5; //bullet height
        Projectile p = new Projectile((int) who.getTranslateX() + (int) who.getWidth() / 2, (int) who.getTranslateY() + (int) who.getHeight() / 2, who);
        //Sprite s = new Sprite((int) who.getTranslateX() + (int) who.getWidth() / 2, (int) who.getTranslateY() + (int) who.getHeight() / 2, bulletW, bulletH, who.type + "bullet", Color.BLACK);
        //root.getChildren().add(s);
        root.getChildren().add(p);
    }

    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(e -> { //movement switch case
            switch (e.getCode()) {
                case LEFT:
                    player.rotateLeft();
                    break;
                case RIGHT:
                    player.rotateRight();
                    break;
                case UP:
                    player.moveForward();
                    break;
                case DOWN:
                    player.moveBackward();
                    break;
                case SPACE:
                    shoot(player);
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

*/