package me.kokumaji.Tamer.Objects.GUIs;

import java.util.ArrayList;
import java.util.UUID;

import me.kokumaji.HibiscusAPI.api.gui.ClickableSlot;
import me.kokumaji.HibiscusAPI.api.gui.GUI;
import me.kokumaji.HibiscusAPI.api.translation.Translator;
import me.kokumaji.Tamer.Objects.EditorCache;
import me.kokumaji.Tamer.Objects.EntityTexture;
import me.kokumaji.Tamer.Tamer;
import me.kokumaji.Tamer.Util.Messages;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import me.kokumaji.Tamer.Util.ClaimingUtil;
import me.kokumaji.Tamer.Util.CustomItem;

public class AccessControlGUI extends GUI {

    private Translator translator;

    protected AccessControlGUI(int pSize, Plugin pPlugin) {
        super(pSize, pPlugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, pPlugin);
        translator = Tamer.GetTranslator();
    }

    @Override
    protected void BuildGUI(Player player) {
        // nop, still not using this lol
    }

    public void BuildEntityGUI(Player p, Entity ent) {
        if (!p.hasPermission("tamer.command.allow")) {
            Messages.Send(p, translator.Translate("general.insufficient-permissions", true));
            return;
        }
        this.MakeInventory("§0» §8Manage Access");
        
        ItemStack placeholder = CustomItem.Create(Material.BLACK_STAINED_GLASS_PANE, " ", 1, true);
        for(int i = 0; i < 9; i++) {
            RegisterSlot(placeholder, i);
            RegisterSlot(placeholder, (getInventory().getSize() - 9) +  i);
        }

        EntityType entType = ent.getType();
        String name = ent.getCustomName() != null ? ent.getCustomName() : WordUtils.capitalize(ent.getType().getName());

        ItemStack entItem = CustomItem.CreateSkull(EntityTexture.valueOf(entType.toString()).GetBase64(), "§7§l" + name,
                1, "&8Health: §b" + (int) ((LivingEntity) ent).getHealth() + "§8/§b"
                        + (int) ((LivingEntity) ent).getMaxHealth());
        ItemMeta meta = entItem.getItemMeta();

        NamespacedKey key = new NamespacedKey(Tamer.GetPlugin(), "entity-id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, ent.getUniqueId().toString());
        entItem.setItemMeta(meta);

        String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=";
        ItemStack addButton = CustomItem.CreateSkull(url, "§b§l§oAdd Player", 1);
        ClickableSlot addPlayer = new ClickableSlot(addButton, getInventory().getSize() - 5);

        RegisterClickable(addPlayer);
        RegisterSlot(entItem, 4);

        ArrayList<OfflinePlayer> players = ClaimingUtil.GetMembers(p, ent);
        int j = 9;
        for(OfflinePlayer offP : players) {
            ItemStack skull = CustomItem.CreateSkull(offP.getUniqueId(), "§3§l" + offP.getName(), 1, "§7Press shift + right click to remove");
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
            Player p = (Player) e.getWhoClicked();

            ItemStack mob = e.getInventory().getItem(4);
            NamespacedKey key = new NamespacedKey(Tamer.GetPlugin(), "entity-id");
            ItemMeta isM = mob.getItemMeta();
            PersistentDataContainer container = isM.getPersistentDataContainer();
            Entity ent = null;

            if (container.has(key, PersistentDataType.STRING)) {
                String id = container.get(key, PersistentDataType.STRING);
                World w = p.getWorld();

                for (Entity entity : w.getEntities()) {
                    if (entity.getUniqueId().equals(UUID.fromString(id))) {
                        ent = entity;
                        break;
                    }
                }

                if (ent == null) {
                    p.sendMessage("§cERROR: Invalid Entity ID");
                    return;
                }
            }
            if(e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                if(e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                    ItemStack is = e.getCurrentItem();
                    ItemMeta isMt = is.getItemMeta();
                    String name = isMt.getDisplayName().substring(4);

                    ClaimingUtil.RemovePlayer(p, ent, name);

                    Bukkit.getScheduler().runTask(this.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                        }
                    });

                    return;
                }
            }

            if(IsClickable(slot)) {
                ClickableSlot cs = GetClickable(slot);

                if(cs.GetName().equals("§b§l§oAdd Player")) {
                    Entity finalEnt = ent;
                    cs.Execute(() -> {

                        EditorCache.AddPlayer(p, finalEnt);

                        Bukkit.getScheduler().runTask(this.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                Messages.Send(p, translator.Translate("entity.enter-username-chat", true));
                                p.closeInventory();
                                p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                            }
                        });

                    });
                }
            }
        }
    }
    
}
