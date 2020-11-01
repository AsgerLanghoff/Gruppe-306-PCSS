import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class Projectile extends Circle {

    final static int MAX_SPEED = 2;
    static final int RADIUS = 5;

    private double xSpeed;
    private double ySpeed;
    public boolean dead = false;
    int angle;
    double angleR;
    int lifespan;

    Projectile(float x, float y, Tank tank) {
        super(x, y, RADIUS); //bruger constructor fra Rectangle som er superclass
        setFill(Color.HOTPINK);
        this.angle = tank.getAngle(); //henter den vinkel tanken har
        this.angleR = Math.toRadians(angle); //laver vinklen til radianer
        //this.player = tank; //gemmer hvilken tank der har affyret
        setSpeed(); //kalder metoden set speed og gemmer en x og y speed
    }

    void setSpeed() {
        //sets x and y speed according the the tanks angle in radians
        this.xSpeed = MAX_SPEED * Math.cos(angleR);
        this.ySpeed = MAX_SPEED * Math.sin(angleR);
    }

    void moveBullet(Map map) {
        this.setCenterX(getCenterX() + xSpeed); //affyrer skud med dens vinkel fra setSpeed
        this.setCenterY(getCenterY() + ySpeed);


        if (this.getCenterX() > map.getTranslateX() && this.getCenterX() < map.getTranslateX() + map.getWidth() && (this.getCenterY() < map.getTranslateY() || this.getCenterY() < map.getTranslateY() + map.getHeight()+RADIUS) && this.getBoundsInParent().intersects(map.getBoundsInParent())) {
            ySpeed = -ySpeed; //inverter hastigheden hvis en top eller bund væg rammes
        } else if (this.getBoundsInParent().intersects(map.getBoundsInParent())) {
            xSpeed = -xSpeed; //inverter speed hvis sidevæg rammes
        }
        lifespan++; //incrementer lifespan hver frame
    }


    Tank collision(List<Tank> tank){ //method that returns a tank that has been hit
        for (int i = 0; i < tank.size(); i++) {
            if (this.getBoundsInParent().intersects(tank.get(i).getBoundsInParent())) {
                return tank.get(i);
            }
        }
        return null;
    }

    public int getLifespan() {
        return lifespan;
    }
}
