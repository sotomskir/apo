package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 17/10/15.
 */
public class MaskCommand extends UndoableCommand implements Command {
    private int[] mask;


    public MaskCommand(ImagePane imagePane, int[] mask) throws Exception {
        super(imagePane);
        this.mask = mask;
    }

    @Override
    public void execute() {
        BufferedImage bi = imagePane.getImage();
        ImageUtils.applyMask(bi, mask);
        imagePane.refresh();
    }

}
