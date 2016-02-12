package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static pl.sotomski.apoz.utils.ImageUtils.getB;
import static pl.sotomski.apoz.utils.ImageUtils.getG;
import static pl.sotomski.apoz.utils.ImageUtils.getR;

/**
 * Line with draggable endpoints.
 */
public class ProfileLine extends Group {
    Line line;
    ImagePane imagePane;
    Endpoint startPoint, endPoint;
    List<Node> nodes = new ArrayList<>();
    IntegerProperty changed = new SimpleIntegerProperty();
    DoubleProperty zoomLevel;
    double x1 = 1, y1 = 1, x2 = 1, y2 = 1;


    public ProfileLine(DoubleProperty zoomLevel, ImagePane imagePane) {
        super();
        this.zoomLevel = zoomLevel;
        zoomLevel.addListener(observable -> update());
        line = new Line();
        line.setStroke(Color.RED);
        startPoint = new Endpoint(line.startXProperty(), line.startYProperty());
        endPoint = new Endpoint(line.endXProperty(), line.endYProperty());
        getChildren().addAll(line, endPoint, startPoint);
        startPoint.enableDrag();
        endPoint.enableDrag();
        changed.setValue(0);
        this.imagePane = imagePane;
    }

    private void createNodes() {
        getChildren().removeAll(nodes);
        nodes.clear();
        if (zoomLevel.getValue() > 6) {
            List<LinePoint> points = getLinePoints();
            for (LinePoint point : points)
                nodes.add(new Node(point.x * zoomLevel.getValue(), point.y * zoomLevel.getValue()));
            getChildren().addAll(nodes);
        }
    }

    public ImagePane getImagePane() {
        return imagePane;
    }

    private void update() {
        startPoint.setCenterX(x1*zoomLevel.getValue());
        startPoint.setCenterY(y1*zoomLevel.getValue());
        endPoint.setCenterX(x2*zoomLevel.getValue());
        endPoint.setCenterY(y2*zoomLevel.getValue());
        createNodes();
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

    public List<Node> getNodes() {
        return nodes;
    }

    public IntegerProperty changedProperty() {
        return changed;
    }

    public List<LinePoint> getLinePoints() {
        int minX, maxX;
        if (getEndX() > getStartX()) { minX = (int) Math.round(getStartX()); maxX = (int) Math.round(getEndX())-1; }
        else { minX = (int) Math.round(getEndX()); maxX = (int) Math.round(getStartX())-1; }
        int sizeX = maxX - minX + 1;
        int minY, maxY;
        if (getEndY() > getStartY()) { minY = (int) Math.round(getStartY()); maxY = (int) Math.round(getEndY())-1; }
        else { minY = (int) Math.round(getEndY()); maxY = (int) Math.round(getStartY())-1; }
        int sizeY = maxY - minY + 1;
        List<LinePoint> points = new ArrayList<>();

        if(sizeX > sizeY) {
            double slope = (getEndY() - getStartY()) / (getEndX() - getStartX());
            for (int x = minX; x <= maxX; ++x) {
                LinePoint point = new LinePoint();
                point.x = x;
                point.y = (int) Math.round(slope * ((double) x - getEndX()) + getEndY());
                points.add(point);
            }
        } else {
            double slope = (getEndY() - getStartY()) / (getEndX() - getStartX());
            for (int y = minY; y <= maxY; ++y) {
                LinePoint point = new LinePoint();
                point.x = (int) Math.round(( ((double) y - getEndY()) / slope) + getEndX());
                point.y = y;
                points.add(point);
            }

//          Reverse array
//            if(getEndY() > getStartY() && getStartX() > getEndX()) {
//                for(int i = 0; i < points.length / 2; i++) {
//                    int[] temp = points[i];
//                    points[i] = points[points.length - i - 1];
//                    points[points.length - i - 1] = temp;
//                }
//            }
        }

        BufferedImage image = getImagePane().getImage();
        for (LinePoint point : points) {
            int rgb = image.getRGB(point.x - 1, point.y - 1);
//            point.r = ImageUtils.getR(rgb);
//            point.g = ImageUtils.getG(rgb);
//            point.b = ImageUtils.getB(rgb);

            point.b = image.getColorModel().getNumComponents() > 1 ? (getR(rgb) + getB(rgb) + getG(rgb)) / 3 : getB(rgb);
        }

        return points;
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

    public class LinePoint {
        public int x, y, r, g, b;
    }

    private class Node extends Circle {
        public Node(double centerX, double centerY) {
            super(centerX, centerY, 3);
            this.setStroke(Color.YELLOW);
            this.setFill(Color.YELLOW);
        }
    }

    private void updateCords() {
        x1 = startPoint.getCenterX() / zoomLevel.getValue();
        y1 = startPoint.getCenterY() / zoomLevel.getValue();
        x2 = endPoint.getCenterX() / zoomLevel.getValue();
        y2 = endPoint.getCenterY() / zoomLevel.getValue();
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
//                getScene().setCursor(Cursor.MOVE);
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
                double maxX = imagePane.getImage().getWidth() * zoomLevel.getValue();
                double maxY = imagePane.getImage().getHeight() * zoomLevel.getValue();
                if (newX > minX && newX < maxX) setCenterX((Math.floor(newX/zoomLevel.getValue())+0.5)*zoomLevel.getValue());
                if (newY > minY && newY < maxY) setCenterY((Math.floor(newY/zoomLevel.getValue())+0.5)*zoomLevel.getValue());
                updateCords();
                createNodes();
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


