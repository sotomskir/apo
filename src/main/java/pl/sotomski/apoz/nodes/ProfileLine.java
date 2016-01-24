package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Line with draggable endpoints.
 */
public class ProfileLine extends Group {
    Line line;
    ImagePane imagePane;
    Endpoint startPoint, endPoint;
    IntegerProperty changed = new SimpleIntegerProperty();
    DoubleProperty zoomLevel;
    double x1, y1, x2, y2;


    public ProfileLine(DoubleProperty zoomLevel, ImagePane imagePane) {
        super();
        this.zoomLevel = zoomLevel;
        zoomLevel.addListener(observable -> update());
        line = new Line();
        line.setStroke(Color.RED);
        startPoint = new Endpoint(line.startXProperty(), line.startYProperty());
        endPoint = new Endpoint(line.endXProperty(), line.endYProperty());
        this.getChildren().addAll(line, endPoint, startPoint);
//        startPoint.enableDrag();
//        endPoint.enableDrag();
        changed.setValue(0);
        this.imagePane = imagePane;
    }

    public ImagePane getImagePane() {
        return imagePane;
    }

    private void update() {
        startPoint.setCenterX(x1*zoomLevel.getValue());
        startPoint.setCenterY(y1*zoomLevel.getValue());
        endPoint.setCenterX(x2*zoomLevel.getValue());
        endPoint.setCenterY(y2*zoomLevel.getValue());
    }

    public void setStartPoint(double startPointX, double startPointY) {
        startPoint.setCenterX(startPointX);
        startPoint.setCenterY(startPointY);
    }

    public void setStart(double startPointX, double startPointY) {
        x1 = (int)(startPointX/zoomLevel.getValue())+0.5;
        y1 = (int)(startPointY/zoomLevel.getValue())+0.5;
        update();
    }

    public void setEnd(double endPointX, double endPointY) {
        x2 = (int)(endPointX/zoomLevel.getValue())+0.5;
        y2 = (int)(endPointY/zoomLevel.getValue())+0.5;
        update();
    }

    public void setEndPoint(double endPointX, double endPointY) {
        endPoint.setCenterX(endPointX);
        endPoint.setCenterY(endPointY);
    }

    public Endpoint getStartPoint() {
        return startPoint;
    }

    public Endpoint getEndPoint() {
        return endPoint;
    }

    public double getStartX() {
        return line.getStartX()/zoomLevel.getValue();
    }

    public double getStartY() {
        return line.getStartY()/zoomLevel.getValue();
    }

    public double getEndX() {
        return line.getEndX()/zoomLevel.getValue();
    }

    public double getEndY() {
        return line.getEndY()/zoomLevel.getValue();
    }

    public int getChanged() {
        return changed.get();
    }

    public IntegerProperty changedProperty() {
        return changed;
    }

    public int[][] getLinePoints() {
        int minX, maxX;
        if (getEndX() > getStartX()) { minX = (int) getStartX(); maxX = (int) getEndX(); }
        else { minX = (int) getEndX(); maxX = (int) getStartX(); }
        int sizeX = maxX - minX + 1;
        int minY, maxY;
        if (getEndY() > getStartY()) { minY = (int) getStartY(); maxY = (int) getEndY(); }
        else { minY = (int) getEndY(); maxY = (int) getStartY(); }
        int sizeY = maxY - minY + 1;
        int[][] points;

        if(sizeX > sizeY) {
            points = new int[sizeX][2];
            double slope = (getEndY() - getStartY()) / (getEndX() - getStartX());
            for (int x = minX; x <= maxX; ++x) {
                points[x - minX][0] = x;
                points[x - minX][1] = (int) (slope * ((double) x - getEndX()) + getEndY());
            }
        } else {
            points = new int[sizeY][2];
            double slope = (getEndY() - getStartY()) / (getEndX() - getStartX());
            for (int y = minY; y <= maxY; ++y) {
                points[y - minY][0] = (int) (( ((double) y - getEndY()) / slope) + getEndX());
                points[y - minY][1] = y;
            }

////          Reverse array
//            if(getEndY() > getStartY() && getStartX() > getEndX()) {
//                for(int i = 0; i < points.length / 2; i++) {
//                    int[] temp = points[i];
//                    points[i] = points[points.length - i - 1];
//                    points[points.length - i - 1] = temp;
//                }
//            }
        }
        return points;
    }

    public int[][] getPixels() {
        BufferedImage image = getImagePane().getImage();
        int channels = image.getColorModel().getNumComponents();
        int[][] points = getLinePoints();
        int[][] pixels = new int[points.length][channels];
        for (int i = 0; i < points.length; ++i) pixels[i] = ImageUtils.getPixel(image, points[i][0], points[i][1]);
        return pixels;
    }

    public boolean isHoverEndpoint() {
        return endPoint.isHover() || startPoint.isHover();
    }

    public DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public DoubleProperty endXProperty() {
        return line.startXProperty();
    }

    public DoubleProperty endYProperty() {
        return line.startYProperty();
    }

    private class Endpoint extends Circle {
        public Endpoint(DoubleProperty x, DoubleProperty y) {
            super(x.getValue(), y.getValue(), 4);
            x.bind(centerXProperty());
            y.bind(centerYProperty());
            this.setStroke(Color.RED);
            this.setFill(Color.rgb(222, 222, 222, 0.1));
        }

        public void enableDrag() {
            final Delta dragDelta = new Delta();

            setOnMousePressed(mouseEvent -> {
                System.out.println("Pressed");
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
                getScene().setCursor(Cursor.MOVE);
            });

            setOnMouseReleased(mouseEvent -> {
                System.out.println("Released " + this.getClass().getName());
                getScene().setCursor(Cursor.DEFAULT);
                changed.set(getChanged()+1);
            });

            setOnMouseDragged(mouseEvent -> {
                System.out.println("Dragged " + this.getClass().getName());
                double newX = mouseEvent.getX() + dragDelta.x;
                double newY = mouseEvent.getY() + dragDelta.y;
                double minX = 0;
                double minY = 0;
                double maxX = getScene().getX() + getScene().getWidth();
                double maxY = getScene().getY() + getScene().getWidth();
                if (newX > minX && newX < maxX) setCenterX(newX);
                if (newY > minY && newY < maxY) setCenterY(newY);
            });

            setOnMouseEntered(mouseEvent -> {
                getScene().setCursor(Cursor.MOVE);
                System.out.println("Entered " + this.getClass().getName());
            });

            setOnMouseExited(mouseEvent -> {
                getScene().setCursor(Cursor.DEFAULT);
                System.out.println("Exited " + this.getClass().getName());
            });
            // records relative x and y co-ordinates.
        }

        class Delta { double x, y; }
    }
}


