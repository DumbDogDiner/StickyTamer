package me.kokumaji.Tamer.Objects;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EditorCache {

    private static HashMap<Player, Entity> cache = new HashMap<Player, Entity>();

    public static void AddPlayer(Player p, Entity e) {
        if(!cache.containsKey(p)) cache.put(p, e);
    }

    public static void RemovePlayer(Player p) {
        if(cache.containsKey(p)) cache.remove(p);
    }

    public static Entity GetEntity(Player p) {
        if(!cache.containsKey(p)) return null;

        return cache.get(p);
    }


}
