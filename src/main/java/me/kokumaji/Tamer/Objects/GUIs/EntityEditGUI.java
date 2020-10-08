package me.kokumaji.Tamer.Objects.GUIs;

import java.util.UUID;

import me.kokumaji.HibiscusAPI.api.gui.ClickableSlot;
import me.kokumaji.HibiscusAPI.api.gui.GUI;
import me.kokumaji.HibiscusAPI.api.translation.Translator;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import me.kokumaji.Tamer.Tamer;
import me.kokumaji.Tamer.Objects.EntityTexture;
import me.kokumaji.Tamer.Util.CustomItem;
import me.kokumaji.Tamer.Util.Messages;

public class EntityEditGUI extends GUI {

    private Translator translator;

    public EntityEditGUI(int pSize, Plugin pPlugin) {
        super(pSize, pPlugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, pPlugin);
        translator = Tamer.getTranslator();
    }

    @Override
    protected void BuildGUI(Player player) {
        // dont use this method, it doesnt work with what I want to do lol
    }

    public void BuildEntityGUI(Player player, Entity ent) {
        if (!player.hasPermission("tamer.command.tool")) {
            Messages.Send(player, translator.Translate("general.insufficient-permissions", true));
            return;
        }

        this.MakeInventory("§0» §8Entity Editor");
        EntityType entType = ent.getType();
        String name = ent.getCustomName() != null ? ent.getCustomName() : WordUtils.capitalize(ent.getType().getName());

        ItemStack entItem = CustomItem.CreateSkull(EntityTexture.valueOf(entType.toString()).getBase64(), "§7§l" + name,
                1, "&8Health: §b" + (int) ((LivingEntity) ent).getHealth() + "§8/§b"
                        + (int) ((LivingEntity) ent).getMaxHealth());
        ItemMeta meta = entItem.getItemMeta();

        NamespacedKey key = new NamespacedKey(Tamer.getInstance(), "entity-id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, ent.getUniqueId().toString());
        entItem.setItemMeta(meta);

        ItemStack options = CustomItem.Create(Material.WRITABLE_BOOK, "§7§lManage Access", 1, true,
                "§8Control who can interact", "§8with this entity.");
        ItemStack abandon = CustomItem.Create(Material.BLAZE_POWDER, "§7§lAbandon This Entity", 1, true,
                "§8Warning: This will remove the", "§8current entity protection!");
        ItemStack close = CustomItem.Create(Material.BARRIER, "§c§lClose Menu", 1, true);

        ClickableSlot optionsCS = new ClickableSlot(options, 12);
        ClickableSlot abandonCS = new ClickableSlot(abandon, 14);
        ClickableSlot closeCS = new ClickableSlot(close, 16);

        ItemStack placeholder = CustomItem.Create(Material.BLACK_STAINED_GLASS_PANE, " ", 1, true);
        for (int i = 0; i < 9; i++) {
            RegisterSlot(placeholder, i);
            RegisterSlot(placeholder, (getInventory().getSize() - 9) + i);
        }

        RegisterSlot(entItem, 10);
        RegisterClickable(optionsCS);
        RegisterClickable(abandonCS);
        RegisterClickable(closeCS);

        Open(player);
    }

    @EventHandler
    @Override
    protected void onSlotClick(InventoryClickEvent e) {
        int slot = e.getSlot();

        if(slot < 0) return;

        if (e.getInventory() != getInventory())
            return;

        if (IsValidSlot(slot)) {
            e.setCancelled(true);
            if (IsClickable(slot)) {
                Player p = (Player) e.getWhoClicked();
                ClickableSlot cs = GetClickable(slot);
                if (cs.GetName().equals("§7§lManage Access"))
                    cs.Execute(() -> {
                        AccessControlGUI gui = (AccessControlGUI) GUIHandler.GetGUI("access");
                        ItemStack mob = e.getInventory().getItem(10);
                        NamespacedKey key = new NamespacedKey(Tamer.getInstance(), "entity-id");
                        ItemMeta isM = mob.getItemMeta();
                        PersistentDataContainer container = isM.getPersistentDataContainer();

                        if (container.has(key, PersistentDataType.STRING)) {
                            String id = container.get(key, PersistentDataType.STRING);
                            World w = p.getWorld();
                            Entity ent = null;

                            for(Entity entity : w.getEntities()) {
                                if(entity.getUniqueId().equals(UUID.fromString(id))) {
                                    ent = entity;
                                    break;
                                }
                            }

                        if(ent == null) {
                            p.sendMessage("§cERROR: Invalid Entity ID");
                            return;
                        }

                        Bukkit.getScheduler().runTask(this.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                p.closeInventory();
                                p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                            }
                        });   

                        gui.BuildEntityGUI(p, ent);
                    }

                  
                });
                if(cs.GetName().equals("§7§lAbandon This Entity")) cs.Execute(() -> {
                    ItemStack mob = e.getInventory().getItem(10);
                    NamespacedKey key = new NamespacedKey(Tamer.getInstance(), "entity-id");
                    ItemMeta isM = mob.getItemMeta();
                    PersistentDataContainer container = isM.getPersistentDataContainer();

                    if(container.has(key, PersistentDataType.STRING)) {
                        String id = container.get(key, PersistentDataType.STRING);
                        World w = p.getWorld();
                        Entity ent = null;

                        for(Entity entity : w.getEntities()) {
                            if(entity.getUniqueId().equals(UUID.fromString(id))) {
                                ent = entity;
                                break;
                            }
                        }

                        if(ent == null) {
                            p.sendMessage("§cERROR: Invalid Entity ID");
                            return;
                        }

                        String data = ent.getPersistentDataContainer().get(CustomItem.GetKey("tamer"), PersistentDataType.STRING);
                        if (data == null) {
                            Messages.Send(p, translator.Translate("entity.entity-unclaimed", true));
                            return;
                        }
                        
                        UUID dataUUID = UUID.fromString(data);
        
                        if(p.getUniqueId().equals(dataUUID)) {
                            Messages.Send(p, translator.Translate("entity.entity-cleared-claim", true));
                            ent.getPersistentDataContainer().remove(CustomItem.GetKey("tamer"));
                            ent.getPersistentDataContainer().remove(CustomItem.GetKey("allowed"));
                        } else {
                            Messages.Send(p, translator.Translate("entity.entity-not-yours", true));
                        }

                        Entity finalEnt = ent;
                        Bukkit.getScheduler().runTask(this.getPlugin(), () -> {
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                            DustOptions dust = new DustOptions(Color.BLACK, 1f);
                            p.spawnParticle(Particle.REDSTONE, finalEnt.getLocation(), 20, 1, 1, 1, dust);
                        });
                    }
                });
                if(cs.GetName().equals("§c§lClose Menu")) cs.Execute(() -> Bukkit.getScheduler().runTask(this.getPlugin(), () -> {
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.ITEM_BOOK_PUT, 1f, 1f);
                }));
            }
        }
    }
    
}
