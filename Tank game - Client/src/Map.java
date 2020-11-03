import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Map extends Rectangle { // defining the map class. the map extends rectangle as the map is build of a series of rectangles each with a placement, width and height.



    Map(int x, int y, int width, int height){
        super();
        setHeight(height);
        setWidth(width);
        setTranslateX(x);
        setTranslateY(y);
    }
}

