package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 17/10/15.
 */
public class ConvertToGrayCommand extends UndoableCommand implements Command {
    private ImagePane imagePane;
    public ConvertToGrayCommand(ImagePane imagePane) {
        super(imagePane);
        this.imagePane = imagePane;
    }

    @Override
    public void execute() {
        imagePane.setImage(ImageUtils.rgbToGrayscale(imagePane.getImage()));
    }

}
