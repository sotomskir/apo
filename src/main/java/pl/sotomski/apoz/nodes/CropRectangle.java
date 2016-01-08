package pl.sotomski.apoz.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CropRectangle extends Group {

    private DoubleProperty zoomLevel = new SimpleDoubleProperty();
    Rectangle r = new Rectangle();
    final double handleRadius = 3;
    Color handleColor = new Color(0,0,0,0);
    List<Shape> resizeHandles = new ArrayList<>();
    int x1, y1, x2, y2;

    public CropRectangle(DoubleProperty zoomLevel) {
        this();
        this.zoomLevel.bind(zoomLevel);
    }

    private void update() {
        int xMin, xMax, yMin, yMax;
        if (x1 < x2) { xMin = x1; xMax = x2; } else { xMin = x2; xMax = x1; }
        if (y1 < y2) { yMin = y1; yMax = y2; } else { yMin = y2; yMax = y1; }
        r.setX(xMin);
        r.setY(yMin);
        r.setWidth(xMax-xMin);
        r.setHeight(yMax-yMin);
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

    public double getX() {
        return r.getX();
    }

    public double getY() {
        return r.getY();
    }

    public double getWidth() {
        return r.getWidth();
    }

    public double getHeight() {
        return r.getHeight();
    }

    CropRectangle(double x, double y, double width, double height) {
        this();
        r.setX(x);
        r.setY(y);
        r.setWidth(width);
        r.setHeight(height);
    }

    public CropRectangle() {
        super();
        this.getChildren().add(r);
        r.setFill(handleColor);
        r.setStroke(Color.RED);

        // move handle:
        Rectangle moveHandle = new Rectangle(0,0,handleColor);
        // bind to bottom center of Rectangle:
        moveHandle.xProperty().bind(r.xProperty().add(handleRadius));
        moveHandle.yProperty().bind(r.yProperty().add(handleRadius));
        moveHandle.widthProperty().bind(r.widthProperty().subtract(handleRadius*2));
        moveHandle.heightProperty().bind(r.heightProperty().subtract(handleRadius*2));

        // top left resize handle:
        CenteredRectangle resizeHandleNW = new CenteredRectangle(handleRadius, handleColor);
        // bind to top left corner of Rectangle:
        resizeHandleNW.centerXProperty().bind(r.xProperty());
        resizeHandleNW.centerYProperty().bind(r.yProperty());

        // bottom right resize handle:
        CenteredRectangle resizeHandleSE = new CenteredRectangle(handleRadius, handleColor);
        // bind to bottom right corner of Rectangle:
        resizeHandleSE.centerXProperty().bind(r.xProperty().add(r.widthProperty()));
        resizeHandleSE.centerYProperty().bind(r.yProperty().add(r.heightProperty()));

        // top right handle:
        CenteredRectangle resizeHandleNE = new CenteredRectangle(handleRadius, handleColor);
        // bind to top right corner of Rectangle:
        resizeHandleNE.centerXProperty().bind(r.xProperty().add(r.widthProperty()));
        resizeHandleNE.centerYProperty().bind(r.yProperty());

        // bottom left resize handle:
        CenteredRectangle resizeHandleSW = new CenteredRectangle(handleRadius, handleColor);
        // bind to bottom left corner of Rectangle:
        resizeHandleSW.centerXProperty().bind(r.xProperty());
        resizeHandleSW.centerYProperty().bind(r.yProperty().add(r.heightProperty()));

        // right handle
        Line resizeHandleE = new Line();
        resizeHandleE.setStrokeWidth(handleRadius*2);
        resizeHandleE.setStroke(handleColor);
        // bind to width of Rectangle
        resizeHandleE.startXProperty().bind(r.xProperty().add(r.widthProperty()));
        resizeHandleE.startYProperty().bind(r.yProperty().add(handleRadius*2));
        resizeHandleE.endXProperty().bind(r.xProperty().add(r.widthProperty()));
        resizeHandleE.endYProperty().bind(r.yProperty().add(r.heightProperty()).subtract(handleRadius*2));

        // right handle
        Line resizeHandleW = new Line();
        resizeHandleW.setStrokeWidth(handleRadius*2);
        resizeHandleW.setStroke(handleColor);
        // bind to wWdth of Rectangle
        resizeHandleW.startXProperty().bind(r.xProperty());
        resizeHandleW.startYProperty().bind(r.yProperty().add(handleRadius*2));
        resizeHandleW.endXProperty().bind(r.xProperty());
        resizeHandleW.endYProperty().bind(r.yProperty().add(r.heightProperty()).subtract(handleRadius*2));

        // right handle
        Line resizeHandleN = new Line();
        resizeHandleN.setStrokeWidth(handleRadius*2);
        resizeHandleN.setStroke(handleColor);
        // bind to wNdth of Rectangle
        resizeHandleN.startXProperty().bind(r.xProperty().add(handleRadius*2));
        resizeHandleN.startYProperty().bind(r.yProperty());
        resizeHandleN.endXProperty().bind(r.xProperty().add(r.widthProperty()).subtract(handleRadius*2));
        resizeHandleN.endYProperty().bind(r.yProperty());

        // right handle
        Line resizeHandleS = new Line();
        resizeHandleS.setStrokeWidth(handleRadius*2);
        resizeHandleS.setStroke(handleColor);
        // bind to wSdth of Rectangle
        resizeHandleS.startXProperty().bind(r.xProperty().add(handleRadius*2));
        resizeHandleS.startYProperty().bind(r.yProperty().add(r.heightProperty()));
        resizeHandleS.endXProperty().bind(r.xProperty().add(r.widthProperty()).subtract(handleRadius*2));
        resizeHandleS.endYProperty().bind(r.yProperty().add(r.heightProperty()));

        resizeHandles.addAll(Arrays.asList(
                resizeHandleNW, resizeHandleNE, resizeHandleSW, resizeHandleSE,
                resizeHandleE, resizeHandleW, resizeHandleN, resizeHandleS, moveHandle)
        );

        // force handles to live in same parent as rectangle:
        for (Shape shape : resizeHandles) this.getChildren().add(shape);

        // setup hover cursors
        moveHandle.setOnMouseEntered(event -> getScene().setCursor(Cursor.MOVE));
        moveHandle.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleNE.setOnMouseEntered(event -> getScene().setCursor(Cursor.NE_RESIZE));
        resizeHandleNE.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleNW.setOnMouseEntered(event -> getScene().setCursor(Cursor.NW_RESIZE));
        resizeHandleNW.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleSE.setOnMouseEntered(event -> getScene().setCursor(Cursor.SE_RESIZE));
        resizeHandleSE.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleSW.setOnMouseEntered(event -> getScene().setCursor(Cursor.SW_RESIZE));
        resizeHandleSW.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleE.setOnMouseEntered(event -> getScene().setCursor(Cursor.E_RESIZE));
        resizeHandleE.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleW.setOnMouseEntered(event -> getScene().setCursor(Cursor.W_RESIZE));
        resizeHandleW.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleN.setOnMouseEntered(event -> getScene().setCursor(Cursor.N_RESIZE));
        resizeHandleN.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        resizeHandleS.setOnMouseEntered(event -> getScene().setCursor(Cursor.S_RESIZE));
        resizeHandleS.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        Wrapper<Point2D> mouseLocation = new Wrapper<>();

        setUpDragging(resizeHandleNE, mouseLocation) ;
        setUpDragging(resizeHandleNW, mouseLocation) ;
        setUpDragging(resizeHandleSE, mouseLocation) ;
        setUpDragging(resizeHandleSW, mouseLocation) ;
        setUpDragging(resizeHandleE, mouseLocation) ;
        setUpDragging(resizeHandleW, mouseLocation) ;
        setUpDragging(resizeHandleN, mouseLocation) ;
        setUpDragging(resizeHandleS, mouseLocation) ;
        setUpDragging(moveHandle, mouseLocation) ;

        resizeHandleNE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = (event.getSceneX() - mouseLocation.value.getX()) / zoomLevel.get();
                double deltaY = (event.getSceneY() - mouseLocation.value.getY()) / zoomLevel.get();
                double newMaxX = r.getX() + r.getWidth() + deltaX ;
                if (newMaxX >= r.getX() && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    r.setWidth(r.getWidth() + deltaX);
                }
                double newY = r.getY() + deltaY ;
                if (newY >= handleRadius && newY <= r.getY() + r.getHeight() - handleRadius) {
                    r.setY(newY);
                    r.setHeight(r.getHeight() - deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleNW.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = (event.getSceneX() - mouseLocation.value.getX()) / zoomLevel.get();
                double deltaY = (event.getSceneY() - mouseLocation.value.getY()) / zoomLevel.get();
                double newX = r.getX() + deltaX ;
                if (newX >= handleRadius && newX <= r.getX() + r.getWidth() - handleRadius) {
                    r.setX(newX);
                    r.setWidth(r.getWidth() - deltaX);
                }
                double newY = r.getY() + deltaY ;
                if (newY >= handleRadius && newY <= r.getY() + r.getHeight() - handleRadius) {
                    r.setY(newY);
                    r.setHeight(r.getHeight() - deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleSE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = (event.getSceneX() - mouseLocation.value.getX()) / zoomLevel.get();
                double deltaY = (event.getSceneY() - mouseLocation.value.getY()) / zoomLevel.get();
                double newMaxX = r.getX() + r.getWidth() + deltaX ;
                if (newMaxX >= r.getX() && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    r.setWidth(r.getWidth() + deltaX);
                }
                double newMaxY = r.getY() + r.getHeight() + deltaY ;
                if (newMaxY >= r.getY() && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    r.setHeight(r.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleSW.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = (event.getSceneX() - mouseLocation.value.getX()) / zoomLevel.get();
                double deltaY = (event.getSceneY() - mouseLocation.value.getY()) / zoomLevel.get();
                double newX = r.getX() + deltaX ;
                if (newX >= handleRadius && newX <= r.getX() + r.getWidth() - handleRadius) {
                    r.setX(newX);
                    r.setWidth(r.getWidth() - deltaX);
                }
                double newMaxY = r.getY() + r.getHeight() + deltaY;
                if (newMaxY >= r.getY() && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    r.setHeight(r.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        moveHandle.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = (event.getSceneX() - mouseLocation.value.getX()) / zoomLevel.get();
                double deltaY = (event.getSceneY() - mouseLocation.value.getY()) / zoomLevel.get();
                double newX = r.getX() + deltaX ;
                double newMaxX = newX + r.getWidth();
                if (newX >= handleRadius && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    r.setX(newX);
                }
                double newY = r.getY() + deltaY ;
                double newMaxY = newY + r.getHeight();
                if (newY >= handleRadius && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    r.setY(newY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }

        });

        resizeHandleE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = (event.getSceneX() - mouseLocation.value.getX()) / zoomLevel.get();
                double newMaxX = r.getX() + r.getWidth() + deltaX;
                if (newMaxX >= r.getX() && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    r.setWidth(r.getWidth() + deltaX);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleW.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = (event.getSceneX() - mouseLocation.value.getX()) / zoomLevel.get();
                double newX = r.getX() + deltaX;
                if (newX >= handleRadius && newX <= r.getX() + r.getWidth() - handleRadius) {
                    r.setX(newX);
                    r.setWidth(r.getWidth() - deltaX);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleN.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaY = (event.getSceneY() - mouseLocation.value.getY()) / zoomLevel.get();
                double newY = r.getY() + deltaY;
                if (newY >= handleRadius && newY <= r.getY() + r.getHeight() - handleRadius) {
                    r.setY(newY);
                    r.setHeight(r.getHeight() - deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleS.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaY = (event.getSceneY() - mouseLocation.value.getY()) / zoomLevel.get();
                double newMaxY = r.getY() + r.getHeight() + deltaY;
                if (newMaxY >= r.getY() && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    r.setHeight(r.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

    }

    private void setUpDragging(Shape shape, Wrapper<Point2D> mouseLocation) {

        shape.setOnDragDetected(event -> {
            shape.getScene().setCursor(Cursor.CLOSED_HAND);
            mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
        });

        shape.setOnMouseReleased(event -> {
            shape.getScene().setCursor(Cursor.DEFAULT);
            mouseLocation.value = null;
        });
    }

    public void setHandlesVisible(boolean value) {
        Color visible = new Color(0,0,0,0.5);
        for (Shape shape : resizeHandles) {
            shape.setFill(value ? visible : handleColor);
            shape.setStroke(value ? visible : handleColor);
        }
    }

    public Color getHandleColor() {
        return handleColor;
    }

    static class Wrapper<T> { T value; }

    class CenteredRectangle extends Rectangle {

        DoubleProperty centerX = new SimpleDoubleProperty();
        DoubleProperty centerY = new SimpleDoubleProperty();


        public CenteredRectangle(double radius, Paint paint) {
            super(radius*2, radius*2, paint);
            xProperty().bind(centerX.subtract(radius));
            yProperty().bind(centerY.subtract(radius));
        }

        public CenteredRectangle() {
            super();
            xProperty().bind(centerX.subtract(widthProperty().divide(2)));
            yProperty().bind(centerY.subtract(heightProperty().divide(2)));
        }

        public double getCenterX() {
            return centerX.get();
        }

        public DoubleProperty centerXProperty() {
            return centerX;
        }

        public double getCenterY() {
            return centerY.get();
        }

        public DoubleProperty centerYProperty() {
            return centerY;
        }

        public void setCenterX(double centerX) {
            this.centerX.set(centerX);
        }

        public void setCenterY(double centerY) {
            this.centerY.set(centerY);
        }
    }

}