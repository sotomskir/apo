package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.sotomski.apoz.controllers.MainController;
import pl.sotomski.apoz.controllers.ToolController;

import java.util.ResourceBundle;

/**
 * Created by sotomski on 29/10/15.
 */
public abstract class Tool extends VBox {

    protected ToolController toolController;
    protected ResourceBundle bundle;
    protected HBox applyCancelBtns;

    protected Tool() {}

    protected Tool(ToolController toolController) {
        this.toolController = toolController;
        bundle = toolController.getBundle();
        Button applyBtn = new Button(bundle.getString("Apply"));
        applyBtn.setOnAction(this::handleApply);
        Button cancelBtn = new Button(bundle.getString("Cancel"));
        cancelBtn.setOnAction((actionEvent) -> handleCancel());
        applyCancelBtns = new HBox(applyBtn, cancelBtn);
    }

    public void setToolController(ToolController toolController) {
        this.toolController = toolController;
    }

    public abstract void handleApply(ActionEvent actionEvent);

    public void handleCancel() {
        disableTool();
    }

    public void disableTool() {
        ((MainController)toolController).disableTools();
    }
}
