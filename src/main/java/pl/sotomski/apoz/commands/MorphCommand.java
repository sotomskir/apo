package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

/**
 * Created by sotomski on 20/11/15.
 */
public class MorphCommand extends UndoableCommand implements Command{
    private int action;

    public MorphCommand(ImagePane image, int action) {
        super(image);
        this.action = action;
    }

    @Override
    public void execute() {
        //Dilatation
        if (action == 0) ImageUtils.dilatation(imagePane.getImage());
        //Erosion
        else if (action == 1) ImageUtils.erosion(imagePane.getImage());
        //Open
        else if (action == 2) ImageUtils.open(imagePane.getImage());
        //Close
        else if (action == 3) ImageUtils.close(imagePane.getImage());
        //Thinning
        else if (action == 4) ImageUtils.thinning(imagePane.getImage());
        //Thickening
        else if (action == 5) ImageUtils.thickening(imagePane.getImage());
        //Outline
        else if (action == 6) ImageUtils.outline(imagePane.getImage());
        //Skeleton
        else if (action == 7) ImageUtils.skeleton(imagePane.getImage());

    }
}
