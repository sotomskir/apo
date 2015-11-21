package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 18/10/15.
 */
public class UndoableCommand {
    protected ImagePane imagePane;
    protected BufferedImage previousImage;

    public UndoableCommand(ImagePane imagePane) {
        this.imagePane = imagePane;
        this.previousImage = ImageUtils.deepCopy(imagePane.getImage());
    }

    public void undo() {
        imagePane.setImage(ImageUtils.deepCopy(previousImage));
    }
}
