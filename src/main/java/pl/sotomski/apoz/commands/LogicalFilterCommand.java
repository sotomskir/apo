package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 17/10/15.
 */
public class LogicalFilterCommand extends UndoableCommand implements Command {
    private int direction;

    public LogicalFilterCommand(ImagePane image, int direction) {
        super(image);
        this.direction = direction;
    }

    @Override
    public void execute() {
        ImageUtils.logicalFilter(imagePane.getImage(), direction);
    }

}
