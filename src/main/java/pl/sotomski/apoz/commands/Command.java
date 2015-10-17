package pl.sotomski.apoz.commands;

/**
 * Created by sotomski on 17/10/15.
 */
public interface Command {
    void execute();
    void undo();
}
