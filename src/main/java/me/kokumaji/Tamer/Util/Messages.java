package me.kokumaji.Tamer.Util;

import java.util.HashMap;

import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messages {

	public static void Send(Player p, String msg, HashMap<String, String> values) {
        StrSubstitutor sub = new StrSubstitutor(values, "{", "}");
        String result = ChatColor.translateAlternateColorCodes('&', sub.replace(msg));
        
        p.sendMessage(result);
    }
    
    public static void Send(Player p,String msg) {
        String result = ChatColor.translateAlternateColorCodes('&', msg);
        p.sendMessage(result);
	}
}