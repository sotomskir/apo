package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.nodes.ImagePane;
import pl.sotomski.apoz.utils.ImageUtils;

public class GradientEdgeDetectionCommand extends UndoableCommand implements Command {

    private final int scalingMethod;
    private final int calcMethod;
    private int bordersMethod;
    private String maskName;
    private int edgeDirection;


    private int[][] robertsMask = {
            {0, 0, 0, 0, 1, 0, 0, 0,-1},
            {0, 0, 0, 0, 0,-1, 0, 1, 0},
    };

    private int[][] sobelMask = {
            {-1, 0, 1, -2, 0, 2, -1, 0, 1},
            {-1, -2, -1, 0, 0, 0, 1, 2, 1}
    };

    private int[][] prewittMask = {
            { 1, 1, 1, 1,-2, 1,-1,-1,-1},//N
            { 1, 1, 1,-1,-2, 1,-1,-1, 1},//NE
            {-1, 1, 1,-1,-2, 1,-1, 1, 1},//E
            {-1,-1, 1,-1,-2, 1, 1, 1, 1},//SE
            {-1,-1,-1, 1,-1, 1, 1, 1, 1},//S
            { 1,-1,-1, 1,-2,-1, 1, 1, 1},//SW
            { 1, 1,-1, 1,-2,-1, 1, 1,-1},//W
            { 1, 1, 1, 1,-2,-1, 1,-1,-1} //NW
    };

    private int[][] kirshMask = {
            { 3, 3, 3, 3, 0, 3,-5,-5,-5},//N
            { 3, 3, 3,-5, 0, 3,-5,-5, 3},//NE
            {-5, 3, 3,-5, 0, 3,-5, 3, 3},//E
            {-5,-5, 3,-5, 0, 3, 3, 3, 3},//SE
            {-5,-5,-5, 3, 0, 3, 3, 3, 3},//S
            { 3,-5,-5, 3, 0,-5, 3, 3, 3},//SW
            { 3, 3,-5, 3, 0,-5, 3, 3,-5},//W
            { 3, 3, 3, 3, 0,-5, 3,-5,-5} //NW
    };

    public GradientEdgeDetectionCommand(ImagePane image, String maskName, int edgeDirection, int bordersMethod, int scalingMethod, int calcMethod) throws Exception {
        super(image);
        this.bordersMethod = bordersMethod;
        this.maskName = maskName;
        this.edgeDirection = edgeDirection;
        this.scalingMethod = scalingMethod;
        this.calcMethod = calcMethod;
    }

    @Override
    public void execute() {
        int[][] mask;
        int[] mask1;
        switch (maskName) {
            case "Roberts":
                mask = robertsMask;
                ImageUtils.gradientFilter(imagePane.getImage(), mask, bordersMethod, scalingMethod, calcMethod);
                break;
            case "Sobel":
                mask = sobelMask;
                ImageUtils.gradientFilter(imagePane.getImage(), mask, bordersMethod, scalingMethod, calcMethod);
                break;
            case "Prewitt":
                mask1 = prewittMask[edgeDirection];
                ImageUtils.linearFilterWithScaling(imagePane.getImage(), mask1, bordersMethod, scalingMethod);
                break;
            case "Kirsh":
                mask1 = kirshMask[edgeDirection];
                ImageUtils.linearFilterWithScaling(imagePane.getImage(), mask1, bordersMethod, scalingMethod);
                break;
            default:
        }
    }

}
