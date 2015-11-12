package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 17/10/15.
 */
public class MaskCommand extends UndoableCommand implements Command {
    private int mask;
    private static int[][] masks = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 8, 1, 1, 1, 1},
            {1, 2, 1, 2, 4, 2, 1, 2, 1},
            {0, 1, 0, 1, 4, 1, 0, 1, 0},
            {-1, -1, -1, -1, 8, -1, -1, -1, -1},
            {0, -1, 0, -1, 4, -1, 0, -1, 0},
            {1, -2, 1, -2, 4, -2, 1, -2, 1},
            {0, 1, 0, 1, -4, 1, 0, 1, 0}};

    public MaskCommand(ImagePane imagePane, int mask) throws Exception {
        super(imagePane);
        if (mask < 0 || mask > 7) throw new IllegalArgumentException("Bad mask. Eligible masks: 0 - 7");
        this.mask = mask;
    }

    @Override
    public void execute() {
        BufferedImage bi = imagePane.getImage();
        ImageUtils.applyMask(bi, masks[mask]);
        imagePane.refresh();
    }

}
