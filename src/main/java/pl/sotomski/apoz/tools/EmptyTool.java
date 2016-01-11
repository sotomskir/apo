package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import pl.sotomski.apoz.controllers.ToolController;

public class EmptyTool extends Tool {

    private static Tool instance;

    protected EmptyTool(ToolController controller) {
        super(controller);
    }

    @Override
    protected void handleApply(ActionEvent actionEvent) {

    }

    public static Tool getInstance(ToolController controller) {
        if(instance == null) instance = new EmptyTool(controller);
        return instance;
    }
}
