package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 20/11/15.
 */
public class MorphCommand extends UndoableCommand implements Command{
    private int action;
    private int neighborhood;

    /**
     *
     * @param image
     * @param action
     * @param neighborhood 0 for square neighborhood 1 for diamond neighborhood
     */
    public MorphCommand(ImagePane image, int action, int neighborhood) {
        super(image);
        this.action = action;
        this.neighborhood = neighborhood;
    }

    @Override
    public void execute() {
        //Dilatation
        if (action == 0) ImageUtils.dilate(imagePane.getImage(), neighborhood);
        //Erosion
        else if (action == 1) ImageUtils.erode(imagePane.getImage(), neighborhood);
        //Open
        else if (action == 2) ImageUtils.open(imagePane.getImage(), neighborhood);
        //Close
        else if (action == 3) ImageUtils.close(imagePane.getImage(), neighborhood);
        //Outline
        else if (action == 4) ImageUtils.outline(imagePane.getImage(), neighborhood);
        //Skeleton
        else if (action == 5) ImageUtils.skeleton(imagePane.getImage(), neighborhood);
    }
}
