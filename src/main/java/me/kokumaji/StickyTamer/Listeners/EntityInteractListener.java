package me.kokumaji.StickyTamer.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.kokumaji.StickyTamer.StickyTamer;
import me.kokumaji.StickyTamer.Util.Messages;
public class EntityInteractListener implements Listener {

    private StickyTamer self = StickyTamer.getPlugin(StickyTamer.class);

    @EventHandler
    public void onEntityBreed(EntityBreedEvent e) {
        if(e.getBreeder() instanceof Player) {
            Player p = (Player) e.getBreeder();
            String uuidString = p.getUniqueId().toString();
            Entity ent1 = e.getFather();
            Entity ent2 = e.getMother();

            PersistentDataContainer persistentDataF = ent1.getPersistentDataContainer();
            PersistentDataContainer persistentDataM = ent2.getPersistentDataContainer();

            String ownerF = persistentDataF.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);
            String ownerM = persistentDataM.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);

            if(ownerF == null || ownerM == null) {
                Messages.CHILD_NOT_PROTECTED.Send(p, true);
                return;
            } else if(!ownerF.equals(uuidString) || !ownerF.equals(uuidString)) {
                Messages.CHILD_NOT_PROTECTED.Send(p, true);
                return;
            }

            Entity ent3 = e.getEntity();
            PersistentDataContainer childContainer = ent3.getPersistentDataContainer();
            childContainer.set(new NamespacedKey(self, "tamer"), PersistentDataType.STRING, uuidString);

            Messages.CHILD_PROTECTED.Send(p, true);
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
        Protectable prot = Protectable.valueOf(entString);
        if(prot != null) {
            Player p = e.getPlayer();
            if(e.getHand().equals(EquipmentSlot.HAND)) {
                ItemStack is = p.getInventory().getItemInMainHand();

                if(is.getType() == null) return;
                
                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                if(p.hasPermission("stickytamer.bypass")) return;
                String data = persistentData.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);
                String dataAllowed = persistentData.get(new NamespacedKey(self, "allowed"), PersistentDataType.STRING);
                if(data == null) return;
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
        Protectable prot = Protectable.valueOf(entString);
        if(prot != null) {
            if(!(e.getDamager() instanceof Player)) return;
            Player p = (Player) e.getDamager();

            if(p.hasPermission("stickytamer.bypass")) return;
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