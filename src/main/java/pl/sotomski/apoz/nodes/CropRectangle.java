package pl.sotomski.apoz.nodes;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 * Created by sotomski on 14/12/15.
 */
public class CropRectangle extends Group {
    Rectangle rectangle = new Rectangle();
    int x1, y1, x2, y2;

    public CropRectangle() {
        super();
        this.getChildren().add(rectangle);
        rectangle.setFill(new Color(0,0,0,0));
        rectangle.setStroke(Color.RED);
    }

    private void update() {
        int xMin, xMax, yMin, yMax;
        if (x1 < x2) { xMin = x1; xMax = x2; } else { xMin = x2; xMax = x1; }
        if (y1 < y2) { yMin = y1; yMax = y2; } else { yMin = y2; yMax = y1; }
        rectangle.setX(xMin);
        rectangle.setY(yMin);
        rectangle.setWidth(xMax-xMin);
        rectangle.setHeight(yMax-yMin);
    }

    public void setEnd(double x, double y) {
        x2 = (int) x;
        y2 = (int) y;
        update();
    }

    public void setStart(double x, double y) {
        x1 = (int) x;
        y1 = (int) y;
        update();
    }


}
