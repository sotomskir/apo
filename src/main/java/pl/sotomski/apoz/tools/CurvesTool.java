package pl.sotomski.apoz.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.LUTCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.CurvesControl;
import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class CurvesTool extends Tool {

    private static CurvesTool instance;
    private CurvesControl curvesControl;

    protected CurvesTool(ToolController controller) {
        super(controller);

        // create controls
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("Curves"));
        curvesControl = new CurvesControl();
        curvesControl.changedProperty().addListener(observable -> updateImageViewAndHistogram());

        getChildren().addAll(separator, label, curvesControl, applyCancelBtns);
        updateImageViewAndHistogram();
    }

    private void updateImageViewAndHistogram() {
        ImagePane ap = toolController.getActivePane();
        BufferedImage image = ImageUtils.applyLUT(toolController.getBufferedImage(), curvesControl.getLUT());
        ap.getImageView().setImage(SwingFXUtils.toFXImage(image, null));
        ap.getHistogramPane().update(image);
    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new CurvesTool(controller);
        return instance;
    }

    @Override
    public void handleApply(ActionEvent actionEvent) {
        ImagePane imagePane = toolController.getActivePane();
        CommandManager manager = imagePane.getCommandManager();
        manager.executeCommand(new LUTCommand(imagePane, curvesControl.getLUT()));
        disableTool();
    }

}
