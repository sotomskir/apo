package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

public class GradientSharpeningCommand extends UndoableCommand implements Command {

    private int bordersMethod;
    private int[][] masks = {
            {-1, -2, -1, 0, 0, 0, 1, 2, 1},
            {-1, 0, 1, -2, 0, 2, -1, 0, 1},
    };

    public GradientSharpeningCommand(ImagePane image, int bordersMethod) {
        super(image);
        this.bordersMethod = bordersMethod;
    }

    @Override
    public void execute() {
        ImageUtils.gradientFilter(imagePane.getImage(), masks, bordersMethod, 3, 0);
    }

}
