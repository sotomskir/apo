package pl.sotomski.apoz.commands;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import pl.sotomski.apoz.nodes.ImagePane;

import java.util.Stack;

/**
 * Created by sotomski on 17/10/15.
 */
public class CommandManager {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private BooleanProperty undoAvailable;
    private BooleanProperty redoAvailable;
    private ImagePane imagePane;

    public CommandManager(ImagePane imagePane) {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        undoAvailable = new SimpleBooleanProperty(false);
        redoAvailable = new SimpleBooleanProperty(false);
        this.imagePane = imagePane;
    }

    public void executeCommand(Command command) {
        long startTime = System.currentTimeMillis();
        command.execute();
        long stopTime = System.currentTimeMillis();
        System.out.println(command.getClass() + ": " + (stopTime-startTime));
        undoStack.push(command);
        undoAvailable.setValue(true);
        if (redoStack.size()>0) {
            redoStack.clear();
            redoAvailable.setValue(false);
        }
        imagePane.refresh();
    }

    public void undo() {
        if (undoStack.size()>0) {
            Command lastCommand = undoStack.pop();
            lastCommand.undo();
            redoStack.push(lastCommand);
            redoAvailable.setValue(true);
            if (undoStack.size()==0) undoAvailable.setValue(false);
        }
        imagePane.refresh();
    }

    public void redo() {
        if (redoStack.size()>0) {
            Command lastCommand = redoStack.pop();
            lastCommand.execute();
            undoStack.push(lastCommand);
            undoAvailable.setValue(true);
            if (redoStack.size()==0) redoAvailable.setValue(false);
        }
        imagePane.refresh();
    }

    public boolean getUndoAvailable() {
        return undoAvailable.get();
    }

    public BooleanProperty undoAvailableProperty() {
        return undoAvailable;
    }

    public boolean getRedoAvailable() {
        return redoAvailable.get();
    }

    public BooleanProperty redoAvailableProperty() {
        return redoAvailable;
    }
}
