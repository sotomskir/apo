package pl.sotomski.apoz.tools;

import pl.sotomski.apoz.controllers.ToolController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sotomski on 29/10/15.
 */
public abstract class ToolFactory {

    private static Map<Class, Object> toolInstances = new HashMap<>();

    public static <T extends Tool> T getInstance(ToolController toolController, Class<T> toolClass) {
        T tool = null;
        if(toolInstances.containsKey(toolClass)) tool = (T) toolInstances.get(toolClass);
        else try {
            tool = toolClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        tool.setToolController(toolController);
        return tool;
    }
}
