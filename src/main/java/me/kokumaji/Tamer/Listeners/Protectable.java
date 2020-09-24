package me.kokumaji.Tamer.Listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

public enum Protectable {
    IRON_GOLEM("IRON_GOLEM"),
    VILLAGER("VILLAGER"),
    HORSE("HORSE");

    private String type;
    Protectable(String str) {
        this.type = str;
    }

    public static boolean IsProtectable(Entity ent) {
        if(ent == null) return false;

        if(ent instanceof Animals) {
            return true;
        } else if(ent instanceof Villager) {
            return true;
        } 

        return false;

        /*for(Protectable s : values()) {
            EntityType entType = EntityType.valueOf(s.type);
            if(entType.equals(ent.getType())) return true;
        }

        return false; */
    }
}