package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 17/10/15.
 */
public class MedianCommand extends UndoableCommand implements Command {
    private int radius;

    public MedianCommand(ImagePane image, int radius) throws Exception {
        super(image);
        this.radius = radius;
    }

    @Override
    public void execute() {
        ImageUtils.medianOperation(imagePane.getImage(), radius);
    }

}
