package me.kokumaji.Tamer.Objects.GUIs;

import java.util.HashMap;
import java.util.Map;

import me.kokumaji.Tamer.Tamer;
import me.kokumaji.Tamer.Objects.GUI;

public class GUIHandler {

    private static Map<String, GUI> guiMap = new HashMap<String, GUI>(); 

    public static void RegisterGUIs() {
        guiMap.put("creative", new EntityEditGUI(27, Tamer.GetPlugin()));
        guiMap.put("access", new AccessControlGUI(27, Tamer.GetPlugin()));
    }

    public static GUI GetGUI(String name) {
        if(guiMap.get(name) != null) 
            return guiMap.get(name);

        return null;
    }
    
}
