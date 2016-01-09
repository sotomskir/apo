package pl.sotomski.apoz.tools;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import pl.sotomski.apoz.commands.CommandManager;
import pl.sotomski.apoz.commands.GradientEdgeDetectionCommand;
import pl.sotomski.apoz.controllers.ToolController;
import pl.sotomski.apoz.nodes.BordersMethodToggles;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.ResourceBundle;

public class GradientEdgeDetectionTool extends VBox {

    private static VBox instance;
    private ToolController toolController;
    private ChoiceBox<String> choiceBox;
    private String[] masks;
    Label masksLabel = new Label();
    ComboBox<String> directionComboBox;
    ComboBox<String> scalingComboBox = new ComboBox<>();
    ComboBox<String> calcMethodComboBox = new ComboBox<>();

    BordersMethodToggles bordersMethodToggles;
    String[] maskLabels = new String[]{" 1  0 |  0 -1\n 0 -1 |  1  0", "-1  0  1 |-1 -2 -1\n-2  0  2 | 0  0  0\n-1  0  1 | 1  2  1"};
    private String[] prewittMask = {
            "  1  1  1\n  1 -2  1\n -1 -1 -1",//N
            "  1  1  1\n -1 -2  1\n -1 -1  1",//NE
            " -1  1  1\n -1 -2  1\n -1  1  1",//E
            " -1 -1  1\n -1 -2  1\n  1  1  1",//SE
            " -1 -1 -1\n  1 -1  1\n  1  1  1",//S
            "  1 -1 -1\n  1 -2 -1\n  1  1  1",//SW
            "  1  1 -1\n  1 -2 -1\n  1  1 -1",//W
            "  1  1  1\n  1 -2 -1\n  1 -1 -1" //NW
    };

    private String[] kirshMask = {
            "  3  3  3\n  3  0  3\n -5 -5 -5",//N
            "  3  3  3\n -5  0  3\n -5 -5  3",//NE
            " -5  3  3\n -5  0  3\n -5  3  3",//E
            " -5 -5  3\n -5  0  3\n  3  3  3",//SE
            " -5 -5 -5\n  3  0  3\n  3  3  3",//S
            "  3 -5 -5\n  3  0 -5\n  3  3  3",//SW
            "  3  3 -5\n  3  0 -5\n  3  3 -5",//W
            "  3  3  3\n  3  0 -5\n  3 -5 -5" //NW
    };
    protected GradientEdgeDetectionTool(ToolController controller) {
        ResourceBundle bundle = controller.getBundle();
        masks = new String[4];
        masks[0] = bundle.getString("RobertsMask");
        masks[1] = bundle.getString("SobelMask");
        masks[2] = bundle.getString("PrewittMask");
        masks[3] = bundle.getString("KirshMask");
        this.choiceBox = new ChoiceBox<>();
        this.toolController = controller;
        Separator separator = new Separator(Orientation.HORIZONTAL);
        Label label = new Label(bundle.getString("EdgeDetection"));
        Label directionLabel = new Label(bundle.getString("EdgeDirection"));
        masksLabel.setText(maskLabels[0]);
        masksLabel.setFont(Font.font("monospace"));

        directionComboBox = new ComboBox<>();
        directionComboBox.getItems().addAll("N", "NE", "E", "SE", "S", "SW", "W", "NW");
        directionComboBox.getSelectionModel().selectFirst();
        directionComboBox.setVisible(false);
        directionComboBox.getSelectionModel().selectedItemProperty().addListener((observable1, oldValue1, newValue1) -> {
            if(masks[2].equals(choiceBox.getSelectionModel().getSelectedItem()))
                masksLabel.setText(prewittMask[directionComboBox.getSelectionModel().getSelectedIndex()]);
            else
                masksLabel.setText(kirshMask[directionComboBox.getSelectionModel().getSelectedIndex()]);
        });
        directionLabel.setVisible(false);

        scalingComboBox.getItems().addAll(
                bundle.getString("scalingMethod0"),
                bundle.getString("scalingMethod1"),
                bundle.getString("scalingMethod2"),
                bundle.getString("scalingMethod3")
                );
        scalingComboBox.getSelectionModel().selectFirst();
        calcMethodComboBox.getItems().addAll(bundle.getString("calcMethod1"), bundle.getString("calcMethod2"));
        calcMethodComboBox.getSelectionModel().selectFirst();

        choiceBox.getItems().addAll(masks);
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(masks[0].equals(newValue)) {
                masksLabel.setText(maskLabels[0]);
            } else if(masks[1].equals(newValue)) {
                masksLabel.setText(maskLabels[1]);
            } else if(masks[2].equals(newValue)) {
                masksLabel.setText(prewittMask[0]);
            } else if(masks[3].equals(newValue)) {
                masksLabel.setText(kirshMask[0]);
            }
            if (masks[2].equals(newValue) || masks[3].equals(newValue)) {
                directionComboBox.setVisible(true);
                directionLabel.setVisible(true);
            }
            else {
                directionComboBox.setVisible(false);
                directionLabel.setVisible(false);
            }
        });

        Button button = new Button(bundle.getString("Apply"));
        button.setOnAction((actionEvent) -> {
            try {
                handleApply(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        bordersMethodToggles = new BordersMethodToggles(bundle);
        getChildren().addAll(
                label,
                separator,
                new Label(bundle.getString("Mask")),
                choiceBox,
                masksLabel,
                directionLabel,
                directionComboBox,
                new Label(bundle.getString("calcMethod")),
                calcMethodComboBox,
                new Label(bundle.getString("scalingMethod")),
                scalingComboBox,
                bordersMethodToggles,
                button
        );
    }

    public static VBox getInstance(ToolController controller) {
        if(instance == null) instance = new GradientEdgeDetectionTool(controller);
        return instance;
    }

    public void handleApply(ActionEvent actionEvent) throws Exception {
        ImagePane imagePane = toolController.getActivePaneProperty();
        CommandManager manager = imagePane.getCommandManager();
        String maskName;
        switch (choiceBox.getSelectionModel().getSelectedIndex()) {
            case 0:
                maskName = "Roberts";
                break;
            case 1:
                maskName = "Sobel";
                break;
            case 2:
                maskName = "Prewitt";
                break;
            case 3:
                maskName = "Kirsh";
                break;
            default:
                maskName = "Roberts";
        }
        int direction = directionComboBox.getSelectionModel().getSelectedIndex();
        int bordersMethod = bordersMethodToggles.getMethod();
        int scalingMethod = scalingComboBox.getSelectionModel().getSelectedIndex();
        int calcMethod = calcMethodComboBox.getSelectionModel().getSelectedIndex();
        manager.executeCommand(new GradientEdgeDetectionCommand(imagePane, maskName, direction, bordersMethod, scalingMethod, calcMethod));
        imagePane.setImage(imagePane.getImage());
    }

}
