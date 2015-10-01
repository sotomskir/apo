package sample;

import javafx.scene.control.Label;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by sotomski on 01/10/15.
 */
public class LabelObserver extends Label implements Observer {
    private String value, textBefore, textAfter;
    @Override
    public void update(Observable o, Object arg) {
        StringBuilder sb = new StringBuilder();
        sb.append(textBefore).append(((ImageObservable)arg).toString()).append(textAfter);
        setText(sb.toString());
    }

    public LabelObserver(String textBefore, String textAfter) {
        this.textBefore = textBefore;
        this.textAfter = textAfter;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }
}
