package me.kokumaji.Tamer.Objects.GUIs;

import java.util.HashMap;
import java.util.Map;

import me.kokumaji.HibiscusAPI.api.gui.GUI;
import me.kokumaji.Tamer.Tamer;

public class GUIHandler {

    private static Map<String, GUI> guiMap = new HashMap<String, GUI>();

    public static void RegisterGUIs() {
        guiMap.put("creative", new EntityEditGUI(27, Tamer.getInstance()));
        guiMap.put("access", new AccessControlGUI(27, Tamer.getInstance()));
    }

    public static GUI GetGUI(String name) {
        if(guiMap.get(name) != null) 
            return guiMap.get(name);

        return null;
    }
    
}
