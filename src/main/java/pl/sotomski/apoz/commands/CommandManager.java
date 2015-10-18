package pl.sotomski.apoz.commands;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Stack;

/**
 * Created by sotomski on 17/10/15.
 */
public class CommandManager {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private BooleanProperty undoAvailable;
    private BooleanProperty redoAvailable;

    public CommandManager() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        undoAvailable = new SimpleBooleanProperty(false);
        redoAvailable = new SimpleBooleanProperty(false);
    }

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        undoAvailable.setValue(true);
        if (redoStack.size()>0) {
            redoStack.clear();
            redoAvailable.setValue(false);
        }
    }

    public void undo() {
        if (undoStack.size()>0) {
            Command lastCommand = undoStack.pop();
            lastCommand.undo();
            redoStack.push(lastCommand);
            redoAvailable.setValue(true);
            if (undoStack.size()==0) undoAvailable.setValue(false);
        }
    }

    public void redo() {
        if (redoStack.size()>0) {
            Command lastCommand = redoStack.pop();
            lastCommand.execute();
            undoStack.push(lastCommand);
            undoAvailable.setValue(true);
            if (redoStack.size()==0) redoAvailable.setValue(false);
        }
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
