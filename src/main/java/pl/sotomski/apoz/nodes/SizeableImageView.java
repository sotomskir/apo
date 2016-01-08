package pl.sotomski.apoz.nodes;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;

/**
 * Pane containing an image that can be resized.
 */
public class SizeableImageView extends ScrollPane {
    /**
     * The zoom factor to be applied for each zoom event.
     *
     * (480th root of 2 means that 12 wheel turns of 40 will result in size factor 2.)
     */
    private static final double ZOOM_FACTOR = 1.0014450997779993488675056142818;

    /**
     * The zoom factor.
     */
    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1000);

    /**
     * The mouse X position.
     */
    private final DoubleProperty mouseXProperty = new SimpleDoubleProperty();

    /**
     * The mouse Y position.
     */
    private final DoubleProperty mouseYProperty = new SimpleDoubleProperty();

    /**
     * Constructor without initialization of image.
     */
    public SizeableImageView() {
        this(new ImageView());
    }

    /**
     * Constructor, initializing with an image view.
     *
     * @param imageView
     *            The ImageView to be displayed.
     */
    public SizeableImageView(final ImageView imageView) {
        StackPane stackPane = new StackPane(imageView);
        setContent(stackPane);

        imageView.setPreserveRatio(true);
        setPannable(true);
        setHbarPolicy(ScrollBarPolicy.ALWAYS);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);

        stackPane.minWidthProperty().bind(Bindings.createDoubleBinding(() ->
                getViewportBounds().getWidth(), viewportBoundsProperty())
        );
        stackPane.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
                getViewportBounds().getHeight(), viewportBoundsProperty())
        );
//        setFitToWidth(true);
//        setFitToHeight(true);

//        setOnMouseMoved(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(final MouseEvent event) {
//                mouseXProperty.set(event.getX());
//                mouseYProperty.set(event.getY());
//            }
//        });

        addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(final ScrollEvent event) {

                // Original size of the image.
                double sourceWidth = zoomProperty.get() * imageView.getImage().getWidth();
                double sourceHeight = zoomProperty.get() * imageView.getImage().getHeight();

                zoomProperty.set(zoomProperty.get() * Math.pow(ZOOM_FACTOR, event.getDeltaY()));

                // Old values of the scrollbars.
                double oldHvalue = getHvalue();
                double oldVvalue = getVvalue();

                // Image pixels outside the visible area which need to be scrolled.
                double preScrollXFactor = Math.max(0, sourceWidth - getWidth());
                double preScrollYFactor = Math.max(0, sourceHeight - getHeight());

                // Relative position of the mouse in the image.
                double centerXImgRelative = (getWidth()/2 + preScrollXFactor * oldHvalue) / sourceWidth;
                double centerYImgRelative = (getHeight()/2 + preScrollYFactor * oldVvalue) / sourceHeight;

                // Target size of the image.
                double targetWidth = zoomProperty.get() * imageView.getImage().getWidth();
                double targetHeight = zoomProperty.get() * imageView.getImage().getHeight();

                // Image pixels outside the visible area which need to be scrolled.
                double postScrollXFactor = Math.max(0, targetWidth - getWidth());
                double postScrollYFactor = Math.max(0, targetHeight - getHeight());

                // Correction applied to compensate the vertical scrolling done by ScrollPane
                double verticalCorrection = (postScrollYFactor / sourceHeight) * event.getDeltaY();

                // New scrollbar positions keeping the mouse position.
                Double newHvalue = ((centerXImgRelative * targetWidth) - getWidth()/2) / postScrollXFactor;
                Double newVvalue =
                        ((centerYImgRelative * targetHeight) - getHeight()/2 + verticalCorrection)
                                / postScrollYFactor;

//                stackPane.setScaleX(zoomProperty.get());
//                stackPane.setScaleY(zoomProperty.get());
                imageView.setFitWidth(targetWidth);
                imageView.setFitHeight(targetHeight);


                System.out.printf("\t%.2f\t%.2f\n", newHvalue, newVvalue);
//                System.out.println("Post:\t"+postScrollXFactor + ":\t"+ postScrollYFactor);

                setHvalue(newHvalue.isNaN() || newHvalue.isInfinite() ? 0 : newHvalue);
                setVvalue(newVvalue.isNaN() || newVvalue.isInfinite() ? 0 : newVvalue);
//                setHvalue(0.5);
//                setVvalue(0.5);
            }
        });

//        addEventFilter(ZoomEvent.ANY, new EventHandler<ZoomEvent>() {
//            @Override
//            public void handle(final ZoomEvent event) {
//                zoomProperty.set(zoomProperty.get() * event.getZoomFactor());
//
//                imageView.setScaleX(zoomProperty.get());
//                imageView.setScaleY(zoomProperty.get());
//            }
//        });

    }

    /**
     * Set the image view displayed by this class.
     *
     * @param imageView
     *            The ImageView.
     */
    public final void setImageView(final ImageView imageView) {
        setContent(imageView);
        zoomProperty.set(Math.min(imageView.getFitWidth() / imageView.getImage().getWidth(), imageView.getFitHeight()
                / imageView.getImage().getHeight()));
    }
}