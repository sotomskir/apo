package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 17/10/15.
 */
public class EdgeDetectionCommand extends UndoableCommand implements Command {
    private final int scalingMethod;
    private int[] mask;
    private int bordersMethod;

    public EdgeDetectionCommand(ImagePane image, int[] mask, int bordersMethod, int scalingMethod) {
        super(image);
        this.mask = mask;
        this.bordersMethod = bordersMethod;
        this.scalingMethod = scalingMethod;
    }

    @Override
    public void execute() {
        ImageUtils.edgeDetectionWithScaling(imagePane.getImage(), mask, bordersMethod, scalingMethod);
    }

}
