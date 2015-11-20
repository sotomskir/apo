package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 17/10/15.
 */
public class MaskCommand extends UndoableCommand implements Command {
    private int[] mask;

    public MaskCommand(ImagePane image, int[] mask) throws Exception {
        super(image);
        this.mask = mask;
    }

    @Override
    public void execute() {
        ImageUtils.applyMask(imagePane.getImage(), mask);
    }

}
