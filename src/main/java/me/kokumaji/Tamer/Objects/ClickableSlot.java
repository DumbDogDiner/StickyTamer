package me.kokumaji.Tamer.Objects;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class ClickableSlot {
    
    private ItemStack is;
    private int slot;

    public ClickableSlot(Material pMaterial, String pName, int pAmount, int pSlot, String... lore) {
        this.is = new ItemStack(pMaterial, pAmount);

        ItemMeta isM = is.getItemMeta();
        isM.setDisplayName(ChatColor.translateAlternateColorCodes('&', pName));
        ArrayList<String> metaLore = new ArrayList<>();

        for (String loreComments : lore) {
            loreComments = ChatColor.translateAlternateColorCodes('&', loreComments);
            metaLore.add(loreComments);
        }

        isM.setLore(metaLore);
        is.setItemMeta(isM);

        this.slot = pSlot;
    }

    public ClickableSlot(ItemStack item, int pSlot) {
        this.is = item;

        this.slot = pSlot;
    }

    public void Execute(Runnable r) {
        new Thread(r).start();
    }

    public ItemMeta GetMeta() {
        return is.getItemMeta();
    }

    public ItemStack GetItem() {
        return is;
    }

    public int GetSlotId() {
        return slot;
    }

    public void SetName(String s) {
        ItemMeta isM = this.is.getItemMeta();
        isM.setDisplayName(s);
        is.setItemMeta(isM);
    }

    public String GetName() {
        return is.getItemMeta().getDisplayName();
    }
}
