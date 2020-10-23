import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Tank extends Rectangle {
    boolean dead = false; //is not used for anything YET
    private int angle = 0; //variable that stores the tanks angle
    private int angleIncrease = 3;
    private int moveSpeed = 3;
    private int wallCollider = 50;
    final static int MAX_PROJECTILES = 10; //max amount of projectiles a player can have at the same time
    private Projectile[] projectiles = new Projectile[MAX_PROJECTILES]; //array with the tanks projectiles
    final String playerID; //either bullet or player-- IS NOT UTILISED YET

    Tank(int x, int y, int w, int h, String playerID, Color color) { //constructor
        super(w, h, color);
        this.playerID = playerID;
        setTranslateX(x);
        setTranslateY(y);
    }

    public Projectile[] getProjectiles() {
        return projectiles; //returns the tanks array of projectiles
    }


    void setDead(){
        dead = true;
    }
    boolean getDead(){
        return dead;
    }

    Projectile shoot() { //shoot method, returns a projectile array or null
        if(!dead) {
            for (int i = 0; i < projectiles.length; i++) {
                if (projectiles[i] == null) { //let's the player shoot if there is an empty space in the array aka. there are less than 10 bullets on the map
                    int k = 60; //offset value

                    //x and y for the center of the tank
                    float centerX = (float) this.getTranslateX() + (float) this.getWidth() / 2; //center of tank
                    float centerY = (float) this.getTranslateY() + (float) this.getHeight() / 2;

                    //cos and sin for the tanks angle
                    float cos = (float) Math.cos(Math.toRadians(getAngle() * -1));
                    float sin = (float) Math.sin(Math.toRadians(getAngle() * -1));

                    //cos and sin multiplied with a constant, calculating the x and y offset
                    float widthOffset = cos * k;
                    float heightOffset = sin * k;

                    //start coordinates for x and y
                    float x = centerX + widthOffset;
                    float y = centerY - heightOffset;

                    Projectile p = new Projectile(x, y, this); //creates a new projectile
                    projectiles[i] = p; //puts the bullets in the bullet array
                    return projectiles[i]; //returns the array, now with bullets in it
                }
            }
        }
            return null;

    }


    void rotateLeft() { //function for rotating a sprite left, pivoting around its middle
        getTransforms().add(new Rotate(-angleIncrease, getX() + getWidth() / 2, getY() + getHeight() / 2));
        angle += -angleIncrease;
    }

    void rotateRight() { //function for rotating a sprite right, pivoting around its middle
        getTransforms().add(new Rotate(+angleIncrease, getX() + getWidth() / 2, getY() + getHeight() / 2));
        angle += angleIncrease;
    }

    void moveForward() {
        double angleR = Math.toRadians(angle);
        setTranslateX(getTranslateX() + moveSpeed * Math.cos(angleR));
        setTranslateY(getTranslateY() + moveSpeed * Math.sin(angleR));
    }

    void moveBackward() {
        double angleR = Math.toRadians(angle);
        setTranslateX(getTranslateX() - moveSpeed * Math.cos(angleR));
        setTranslateY(getTranslateY() - moveSpeed * Math.sin(angleR));
    }

    public boolean isColliding(Map[] maps){ //method that returns true if colliding with a wall
        for (int i = 0; i < maps.length; i++) {
            if (this.getBoundsInParent().intersects(maps[i].getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    public int getAngle() {
        return angle;
    }
}
