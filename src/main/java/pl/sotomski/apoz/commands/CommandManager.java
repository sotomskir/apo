package pl.sotomski.apoz.commands;

import pl.sotomski.apoz.commands.Command;

import java.util.Stack;

/**
 * Created by sotomski on 17/10/15.
 */
public class CommandManager {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;

    public CommandManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        if (redoStack.size()>0) redoStack.clear();
    }

    public void undo() {
        if (undoStack.size()>0) {
            Command lastCommand = undoStack.pop();
            lastCommand.undo();
            redoStack.push(lastCommand);
        }
    }

    public void redo() {
        if (redoStack.size()>0) {
            Command lastCommand = redoStack.pop();
            lastCommand.execute();
            undoStack.push(lastCommand);
        }
    }

    public boolean isUndoAvailable() {
        return undoStack.size() > 0;
    }

   public boolean isRedoAvailable() {
        return redoStack.size() > 0;
    }
}
