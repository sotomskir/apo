package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 17/10/15.
 */
public class LinearFilterCommand extends UndoableCommand implements Command {
    private int[] mask;
    private int bordersMethod;

    public LinearFilterCommand(ImagePane image, int[] mask, int bordersMethod) throws Exception {
        super(image);
        this.mask = mask;
        this.bordersMethod = bordersMethod;
    }

    @Override
    public void execute() {
        ImageUtils.linearFilter(imagePane.getImage(), mask, bordersMethod);
    }

}
