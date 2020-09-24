package me.kokumaji.Tamer.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.kokumaji.Tamer.Tamer;
import me.kokumaji.Tamer.Listeners.Protectable;
import me.kokumaji.Tamer.Util.MojangUtil.MojangUser;

public class ClaimingUtil {

    public static void ClaimEntity(Player p, Entity ent) {
                
        if (ent == null) {
            Messages.NO_ENTITY_FOUND.Send(p, true);
            return;
        }

        if(!Protectable.IsProtectable(ent)) {
            Messages.CANT_PROTECT.Send(p, true);
            return;
        }
        PersistentDataContainer persistentData = ent.getPersistentDataContainer();
        String data = persistentData.get(new NamespacedKey(Tamer.GetPlugin(), "tamer"), PersistentDataType.STRING);
        if (data != null) {
            Messages.ENTITY_ALREADY_CLAIMED.Send(p, true, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put("OwnerUUID", data);
                    put("OwnerName", Bukkit.getOfflinePlayer(UUID.fromString(data)).getName());
                }
            });
            return;
        }
        persistentData.set(new NamespacedKey(Tamer.GetPlugin(), "tamer"), PersistentDataType.STRING,
                p.getUniqueId().toString());
        String entityString = ent.getType().toString();
        String sound = "ENTITY_" + entityString + "_AMBIENT";

        p.playSound(p.getLocation(), Sound.valueOf(sound), 0.5f, 1.5f);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
        p.spawnParticle(Particle.HEART, ent.getLocation(), 20, 0.5, 0.5, 0.5);
        Messages.ENTITY_LINK_SUCCESS.Send(p, true);
    }

    public static boolean AddUser(Player sender, Entity ent, String other) {
        if (ent == null) {
            Messages.NO_ENTITY_FOUND.Send(sender, true);
            return false;
        }

        PersistentDataContainer persistentData = ent.getPersistentDataContainer();
        String data = persistentData.get(new NamespacedKey(Tamer.GetPlugin(), "allowed"), PersistentDataType.STRING);
        ArrayList<String> playerList = new ArrayList<String>();
        if(data != null) {
            playerList = new ArrayList<>(Arrays.asList(data.split(",")));
        }
        if(playerList != null && playerList.size() == 8) {
            Messages.ALLOW_CMD_ERROR_FULL_LIST.Send(sender, true);
            return false;
        }

        
        MojangUtil request = new MojangUtil();
        MojangUser allowed = request.resolveUser(other);

        
        if(allowed == null) {
            Messages.PLAYER_NOT_EXISTS.Send(sender, true, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put("Player", other);
                }
            });
            return false;
        } else if(allowed.getUUID() == sender.getUniqueId()) {
            Messages.CANT_ADD_SELF.Send(sender, true);
            return false;
        } else {
            OfflinePlayer offP = Bukkit.getOfflinePlayer(allowed.getUUID());

            if(!offP.hasPlayedBefore()) {
                Messages.PLAYER_NEVER_JOINED.Send(sender, true, new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put("Player", allowed.getName());
                    }
                });    
                return false;
            }

            playerList.add(allowed.getUUID().toString());
            String listString = String.join(",", playerList);

            persistentData.set(new NamespacedKey(Tamer.GetPlugin(), "allowed"), PersistentDataType.STRING, listString);
            Messages.ADDED_PLAYER.Send(sender, true, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put("Player", allowed.getName());
                }
            });
        }

        return true;
    }

    public static ArrayList<OfflinePlayer> GetMembers(Player p, Entity ent) {
        ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
                                        
        if (ent == null) {
            Messages.NO_ENTITY_FOUND.Send(p, true);
            return players;
        }

        PersistentDataContainer persistentData = ent.getPersistentDataContainer();
        String data = persistentData.get(new NamespacedKey(Tamer.GetPlugin(), "allowed"), PersistentDataType.STRING);

        if(data != null) {
            ArrayList<String> playerList = new ArrayList<>(Arrays.asList(data.split(",")));
            for(String s : playerList) {
                s = s.replace(",", "");
                OfflinePlayer offP = Bukkit.getOfflinePlayer(UUID.fromString(s));

                if(offP == null) continue;
                players.add(offP);
            }
        } else {
            return players;
        }

        return players;
    }

    public static boolean IsOwner(UUID uuid, Entity ent) {

        if (ent == null) {
            return false;
        }

        PersistentDataContainer persistentData = ent.getPersistentDataContainer();
        String data = persistentData.get(new NamespacedKey(Tamer.GetPlugin(), "tamer"), PersistentDataType.STRING);
        if (data == null) {
            return false;
        }
        
        UUID dataUUID = UUID.fromString(data);

        if(uuid.equals(dataUUID)) {
            return true;
        } 

        return false;

    }
    
}
