package me.kokumaji.Tamer.Objects.GUIs;

import java.util.ArrayList;

import me.kokumaji.HibiscusAPI.api.gui.ClickableSlot;
import me.kokumaji.HibiscusAPI.api.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.kokumaji.Tamer.Util.ClaimingUtil;
import me.kokumaji.Tamer.Util.CustomItem;

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

        String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=";
        ItemStack addButton = CustomItem.CreateSkull(url, "§b§l§oAdd Player", 1);
        ClickableSlot addPlayer = new ClickableSlot(addButton, getInventory().getSize() - 5);

        RegisterClickable(addPlayer);

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

        if(slot < 0) return;
        
        if(e.getInventory() != this.getInventory()) 
        return;

        if(IsValidSlot(slot)) {
            e.setCancelled(true);
            ClickableSlot cs = GetClickable(slot);

            if(cs.GetName().equals("§b§l§oAdd Player")) {
                cs.Execute(() -> {
                    // todo: implement stuff here
                });
            }
            
        }
    }
    
}
