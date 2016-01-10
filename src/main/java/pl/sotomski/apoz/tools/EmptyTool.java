package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.controllers.ToolController;

public class EmptyTool extends Tool {

    private static VBox instance;

    protected EmptyTool(ToolController controller) {
        super(controller);
    }

    @Override
    protected void handleApply(ActionEvent actionEvent) {

    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new EmptyTool(controller);
        return instance;
    }
}
