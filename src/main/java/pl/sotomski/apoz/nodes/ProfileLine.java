package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Line with draggable endpoints.
 */
public class ProfileLine extends Group {
    Line line;
    Endpoint startPoint, endPoint;

    public ProfileLine() {
        super();
        line = new Line();
        startPoint = new Endpoint(line.startXProperty(), line.startYProperty());
        endPoint = new Endpoint(line.endXProperty(), line.endYProperty());
        this.getChildren().addAll(line, endPoint, startPoint);
        startPoint.enableDrag();
        endPoint.enableDrag();
    }

    public void setStartPoint(double startPointX, double startPointY) {
        startPoint.setCenterX(startPointX);
        startPoint.setCenterY(startPointY);
    }

    public void setEndPoint(double endPointX, double endPointY) {
        endPoint.setCenterX(endPointX);
        endPoint.setCenterY(endPointY);
    }

    public double getStartX() {
        return line.getStartX();
    }

    public double getStartY() {
        return line.getStartY();
    }

    public boolean isHoverEndpoint() {
        return endPoint.isHover() || startPoint.isHover();
    }

    private class Endpoint extends Circle {
        public Endpoint(DoubleProperty x, DoubleProperty y) {
            super(x.getValue(), y.getValue(), 4);
            x.bind(centerXProperty());
            y.bind(centerYProperty());
            this.setStroke(Color.BLACK);
            this.setFill(Color.rgb(222, 222, 222));
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


