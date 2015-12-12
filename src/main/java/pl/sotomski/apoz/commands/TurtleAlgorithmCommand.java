package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 17/10/15.
 */
public class TurtleAlgorithmCommand extends UndoableCommand implements Command {
    private ImagePane imagePane;
    int objectColor;

    public TurtleAlgorithmCommand(ImagePane imagePane, int objectColor) {
        super(imagePane);
        this.imagePane = imagePane;
        this.objectColor = objectColor;
    }

    @Override
    public void execute() {
        ImageUtils.turtleAlgorithm(imagePane.getImage(), objectColor);
    }

}
