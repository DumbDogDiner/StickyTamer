package me.kokumaji.Tamer.Listeners;

import com.mojang.brigadier.Message;

import me.kokumaji.HibiscusAPI.api.translation.Translator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.kokumaji.Tamer.Tamer;
import me.kokumaji.Tamer.Objects.GUIs.EntityEditGUI;
import me.kokumaji.Tamer.Objects.GUIs.GUIHandler;
import me.kokumaji.Tamer.Util.ClaimingUtil;
import me.kokumaji.Tamer.Util.Messages;

public class EntityInteractListener implements Listener {

    private Tamer self = Tamer.getPlugin(Tamer.class);
    private static Translator translator = Tamer.GetTranslator();

    @EventHandler
    public void onEntityBreed(EntityBreedEvent e) {
        if (e.getBreeder() instanceof Player) {
            Player p = (Player) e.getBreeder();
            String uuidString = p.getUniqueId().toString();
            Entity ent1 = e.getFather();
            Entity ent2 = e.getMother();

            PersistentDataContainer persistentDataF = ent1.getPersistentDataContainer();
            PersistentDataContainer persistentDataM = ent2.getPersistentDataContainer();

            String ownerF = persistentDataF.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);
            String ownerM = persistentDataM.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);

            if (ownerF == null || ownerM == null) {
                Messages.Send(p, translator.Translate("entity.child-not-protected", true));
                return;
            } else if (!ownerF.equals(uuidString) || !ownerF.equals(uuidString)) {
                Messages.Send(p, translator.Translate("entity.child-not-protected", true));
                return;
            }

            Entity ent3 = e.getEntity();
            PersistentDataContainer childContainer = ent3.getPersistentDataContainer();
            childContainer.set(new NamespacedKey(self, "tamer"), PersistentDataType.STRING, uuidString);

            Messages.Send(p, translator.Translate("entity.child-protected", true));
            String entityString = ent3.getType().toString();
            String sound = "ENTITY_" + entityString + "_AMBIENT";

            p.playSound(p.getLocation(), Sound.valueOf(sound), 0.5f, 1.5f);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
            p.spawnParticle(Particle.HEART, ent3.getLocation(), 20, 0.5, 0.5, 0.5);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Entity ent = e.getRightClicked();
        String entString = ent.getType().toString();
        boolean prot = Protectable.IsProtectable(ent);
        if (prot) {
            Player p = e.getPlayer();
            if (e.getHand().equals(EquipmentSlot.HAND)) {
                ItemStack is = p.getInventory().getItemInMainHand();
                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                String data = persistentData.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);
                String dataAllowed = persistentData.get(new NamespacedKey(self, "allowed"), PersistentDataType.STRING);

                if (is.getType() == null) {
                    return;
                } else if (is.getType() == Material.BOOK) {
                    NamespacedKey key = new NamespacedKey(Tamer.GetPlugin(), "is-tool");
                    ItemMeta isM = is.getItemMeta();
                    PersistentDataContainer container = isM.getPersistentDataContainer();
                    if (container.has(key, PersistentDataType.INTEGER)) {
                        if (container.get(key, PersistentDataType.INTEGER) != 1)
                            return;

                        if (data == null) {
                            if(p.isSneaking()) {
                                ClaimingUtil.ClaimEntity(p, ent);
                                p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                            } else {
                                Messages.Send(p, translator.Translate("entity.entity-unclaimed-tutorial", true));
                                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.75f, 1f);
                            }
                            return;
                        }
                        
                        if(!(p.getUniqueId().toString().equals(data))) {
                            e.setCancelled(true);
                            p.sendMessage(ChatColor.RED + "You can not interact with this entity!");
                            return;
                        }

                        p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                        Messages.Send(p, translator.Translate("gui.open-edit-menu", true));
                        EntityEditGUI gui = (EntityEditGUI) GUIHandler.GetGUI("creative");

                        gui.BuildEntityGUI(p, ent);

                        return;
                    }
                }

                if(p.hasPermission("Tamer.bypass")) return;

                if(data == null) return;

                if(dataAllowed == null) {
                    if(!(p.getUniqueId().toString().equals(data))) {
                        p.sendMessage(ChatColor.RED + "You can not interact with this entity!"); 
                        e.setCancelled(true);
                    }

                    return;
                };

                if(dataAllowed.contains(p.getName())) return;

                if(!(p.getUniqueId().toString().equals(data))) {
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.RED + "You can not interact with this entity!");
                } 

                return;
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity ent = e.getEntity();
        String entString = ent.getType().toString();
        boolean prot = Protectable.IsProtectable(ent);
        if(prot) {
            if(!(e.getDamager() instanceof Player)) return;
            Player p = (Player) e.getDamager();

            if(p.hasPermission("Tamer.bypass")) return;
            //todo: change this to include members
            PersistentDataContainer persistentData = ent.getPersistentDataContainer();
            String data = persistentData.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);
            String dataAllowed = persistentData.get(new NamespacedKey(self, "allowed"), PersistentDataType.STRING);
            if(data == null) return;
            if(dataAllowed != null && dataAllowed.contains(p.getUniqueId().toString())) return;

            if(!(p.getUniqueId().toString().equals(data))) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "You can not interact with this entity!");
            }
        }
    }
    
}