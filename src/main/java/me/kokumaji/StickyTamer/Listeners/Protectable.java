package me.kokumaji.StickyTamer.Listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;

public enum Protectable {
    IRON_GOLEM("IRON_GOLEM"),
    VILLAGER("VILLAGER"),
    HORSE("HORSE");

    private String type;
    Protectable(String str) {
        this.type = str;
    }

    public static boolean IsProtectable(Entity ent) {
        return ent instanceof Animals;

        /*for(Protectable s : values()) {
            EntityType entType = EntityType.valueOf(s.type);
            if(entType.equals(ent.getType())) return true;
        }

        return false; */
    }
}