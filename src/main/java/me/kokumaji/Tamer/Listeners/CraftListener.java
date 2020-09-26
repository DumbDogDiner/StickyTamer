package me.kokumaji.Tamer.Listeners;

import me.kokumaji.Tamer.Util.CustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        
        for(ItemStack is : e.getInventory().getMatrix()) {
            if(is == null) continue;
            if(CustomItem.IsBook(is)) {
                e.setCancelled(true);
                e.getInventory().remove(is);
            }
        }

    }
    
}
