package me.kokumaji.Tamer.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import me.kokumaji.HibiscusAPI.api.translation.Translator;
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
        Translator translator = Tamer.GetTranslator();

        if (ent == null) {
            Messages.Send(p, translator.Translate("entity.no-entity-found", true));
            return;
        }

        if(!Protectable.IsProtectable(ent)) {
            Messages.Send(p, translator.Translate("entity.cant-protect", true));
            return;
        }
        PersistentDataContainer persistentData = ent.getPersistentDataContainer();
        String data = persistentData.get(new NamespacedKey(Tamer.GetPlugin(), "tamer"), PersistentDataType.STRING);
        if (data != null) {
            Messages.Send(p, translator.Translate("entity.entity-already-claimed", true), new HashMap<String, String>() {
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
        Messages.Send(p, translator.Translate("entity.entity-link-success", true));
    }

    public static void AddUser(Player sender, Entity ent, String other) {
        Translator translator = Tamer.GetTranslator();
        if (ent == null) {
            Messages.Send(sender, translator.Translate("entity.no-entity-found", true));
            return;
        }

        PersistentDataContainer persistentData = ent.getPersistentDataContainer();
        String data = persistentData.get(new NamespacedKey(Tamer.GetPlugin(), "allowed"), PersistentDataType.STRING);
        ArrayList<String> playerList = new ArrayList<String>();
        if(data != null) {
            playerList = new ArrayList<>(Arrays.asList(data.split(",")));
        }
        if(playerList != null && playerList.size() == 8) {
            Messages.Send(sender, translator.Translate("command.allow-cmd-full-list", true));
            return;
        }

        
        MojangUtil request = new MojangUtil();
        MojangUser allowed = request.resolveUser(other);

        
        if(allowed == null) {
            Messages.Send(sender, translator.Translate("command.player-not-exists", true), new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put("Player", other);
                }
            });
        } else if(allowed.getUUID() == sender.getUniqueId()) {
            Messages.Send(sender, translator.Translate("command.cant-add-self", true));
        } else {
            OfflinePlayer offP = Bukkit.getOfflinePlayer(allowed.getUUID());

            if(!offP.hasPlayedBefore()) {
                Messages.Send(sender, translator.Translate("command.player-never-joined", true), new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put("Player", allowed.getName());
                    }
                });
                return;
            }

            playerList.add(allowed.getUUID().toString());
            String listString = String.join(",", playerList);

            persistentData.set(new NamespacedKey(Tamer.GetPlugin(), "allowed"), PersistentDataType.STRING, listString);
            Messages.Send(sender, translator.Translate("command.added-player", true), new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put("Player", allowed.getName());
                }
            });
        }

    }

    public static ArrayList<OfflinePlayer> GetMembers(Player p, Entity ent) {
        Translator translator = Tamer.GetTranslator();
        ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
                                        
        if (ent == null) {
            Messages.Send(p, translator.Translate("entity.no-entity-found", true));
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

        return uuid.equals(dataUUID);

    }
    
}
