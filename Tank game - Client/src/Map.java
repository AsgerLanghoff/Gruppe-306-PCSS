import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Map extends Rectangle {
    //final static Color color = Color.GRAY;
    //final static int width = 1200;
    //final static int height = 5;



    Map(int x, int y, int width, int height){
        super();
        setHeight(height);
        setWidth(width);
        setTranslateX(x);
        setTranslateY(y);
    }
}

