package me.kokumaji.StickyTamer.Objects.GUIs;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.kokumaji.StickyTamer.Objects.ClickableSlot;
import me.kokumaji.StickyTamer.Objects.GUI;
import me.kokumaji.StickyTamer.Util.ClaimingUtil;
import me.kokumaji.StickyTamer.Util.CustomItem;

public class AccessControlGUI extends GUI {

    protected AccessControlGUI(int pSize, Plugin pPlugin) {
        super(pSize, pPlugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, pPlugin);
    }

    @Override
    protected void BuildGUI(Player player) {
        // nop, still not using this lol
    }

    public void BuildEntityGUI(Player p, Entity ent) {
        this.MakeInventory("§0» §8Manage Access");
        
        ItemStack placeholder = CustomItem.Create(Material.BLACK_STAINED_GLASS_PANE, " ", 1, true);
        for(int i = 0; i < 9; i++) {
            RegisterSlot(placeholder, i);
            RegisterSlot(placeholder, (getInventory().getSize() - 9) +  i);
        }

        ArrayList<OfflinePlayer> players = ClaimingUtil.GetMembers(p, ent);
        int j = 9;
        for(OfflinePlayer offP : players) {
            ItemStack skull = CustomItem.CreateSkull(offP.getUniqueId(), "§3§l" + offP.getName(), 1);
            RegisterSlot(skull, j);
            j++;
        }

        Open(p);
    }

    @Override
    @EventHandler
    protected void onSlotClick(InventoryClickEvent e) {
        int slot = e.getSlot();
        
        if(e.getInventory() != this.getInventory()) 
        return;

        if(IsValidSlot(slot)) {
            e.setCancelled(true);
            ClickableSlot cs = GetClickable(slot);
            
        }
    }
    
}
