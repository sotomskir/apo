package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Created by sotomski on 17/10/15.
 */
public class BinaryOperationCommand extends UndoableCommand implements Command {
    private String method;
    private BufferedImage secondImage;

    public BinaryOperationCommand(ImagePane image, BufferedImage secondImage, String method) throws Exception {
        super(image);
        this.method = method;
        this.secondImage = secondImage;
    }

    @Override
    public void execute() {
        BufferedImage image = imagePane.getImage();
        System.out.println(method);
        imagePane.setImage(ImageUtils.binaryOperation(image, secondImage, method));
    }


}
