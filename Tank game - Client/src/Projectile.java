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
        super(x, y, RADIUS); //uses constructor from the rectangle superclass
        setFill(Color.HOTPINK);
        this.angle = tank.getAngle(); //gets the angle of the tank
        this.angleR = Math.toRadians(angle); //converts the angle to radians
        setSpeed(); //calls the method setSpeed and saves a x and y speed
    }

    void setSpeed() {
        //sets x and y speed according the the tanks angle in radians
        this.xSpeed = MAX_SPEED * Math.cos(angleR);
        this.ySpeed = MAX_SPEED * Math.sin(angleR);
    }

    void moveBullet(Map map) {
        this.setCenterX(getCenterX() + xSpeed); //shoots with angle from setSpeed.
        this.setCenterY(getCenterY() + ySpeed);


        if (this.getCenterX() > map.getTranslateX() && this.getCenterX() < map.getTranslateX() + map.getWidth() && (this.getCenterY() < map.getTranslateY() || this.getCenterY() < map.getTranslateY() + map.getHeight()+RADIUS) && this.getBoundsInParent().intersects(map.getBoundsInParent())) {
            ySpeed = -ySpeed; //inverting speed if top or bottom of wall is hit
        } else if (this.getBoundsInParent().intersects(map.getBoundsInParent())) {
            xSpeed = -xSpeed; //Inverting speed if side of wall is hit
        }
        lifespan++; //incrementing lifespan each frame
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
