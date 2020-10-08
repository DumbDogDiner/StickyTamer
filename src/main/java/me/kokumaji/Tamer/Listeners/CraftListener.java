package me.kokumaji.Tamer.Listeners;

import me.kokumaji.Tamer.Util.CustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    @EventHandler
    public void onCraft(PlayerInteractEvent e) {

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack is = e.getItem();

            if(is == null) return;

            if(CustomItem.IsTool(is)) {
                e.setCancelled(true);
            }
        }

    }
    
}
