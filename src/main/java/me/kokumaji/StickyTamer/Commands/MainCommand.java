package me.kokumaji.StickyTamer.Commands;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import me.kokumaji.StickyTamer.StickyTamer;
import me.kokumaji.StickyTamer.Util.ClaimingUtil;
import me.kokumaji.StickyTamer.Util.CustomItem;
import me.kokumaji.StickyTamer.Util.Messages;
import me.kokumaji.StickyTamer.Util.MojangUtil;
import me.kokumaji.StickyTamer.Util.MojangUtil.MojangUser;

public class MainCommand implements CommandExecutor, TabCompleter {

    private StickyTamer self = StickyTamer.getPlugin(StickyTamer.class);
    private String[] argumentPool = {
        "info",
        "claim",
        "unclaim",
        "allow",
        "deny",
        "list",
        "book"
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 0) {
                Messages.ABOUT_PLUGIN.Send(p, true, new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put("PluginName", self.getDescription().getName());
                        put("PluginVersion", self.getDescription().getVersion());
                        put("PluginAuthor", self.getDescription().getAuthors().get(0));
                    }
                });
            } else if (args[0].equalsIgnoreCase("info")) {
                if (!p.hasPermission("stickytamer.info")) {
                    Messages.INSUFFICIENT_PERMS.Send(p, true);
                    return true;
                }
                Entity ent = GetInSight(p, 10);
                if (ent == null) {
                    Messages.NO_ENTITY_FOUND.Send(p, true);
                    return true;
                }

                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                String data = persistentData.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);
                if (data == null) {
                    Messages.ENTITY_UNCLAIMED.Send(p, true);
                    return true;
                }
                Messages.ENTITY_OWNER.Send(p, true, new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;

                    {
                        put("OwnerUUID", data);
                        put("OwnerName", Bukkit.getOfflinePlayer(UUID.fromString(data)).getName());
                        put("Entity", ent.getType().toString());
                    }
                });
            } else if (args[0].equalsIgnoreCase("claim")) {
                Entity ent = GetInSight(p, 10);

                ClaimingUtil.ClaimEntity(p, ent);
                
            } else if (args[0].equalsIgnoreCase("unclaim")) {
                Entity ent = GetInSight(p, 10);
                
                if (ent == null) {
                    Messages.NO_ENTITY_FOUND.Send(p, true);
                    return true;
                }

                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                String data = persistentData.get(new NamespacedKey(self, "tamer"), PersistentDataType.STRING);
                if (data == null) {
                    Messages.ENTITY_UNCLAIMED.Send(p, true);
                    return true;
                }
                
                UUID dataUUID = UUID.fromString(data);

                System.out.println(dataUUID);
                System.out.println(p.getUniqueId());
                System.out.println(p.getUniqueId().equals(dataUUID));

                if(p.getUniqueId().equals(dataUUID)) {
                    Messages.ENITTY_CLEARED_CLAIM.Send(p, true);
                    persistentData.remove(new NamespacedKey(self, "tamer"));
                    persistentData.remove(new NamespacedKey(self, "allowed"));
                } else {
                    Messages.ENTITY_NOT_YOURS.Send(p, true);
                }
            } else if(args[0].equalsIgnoreCase("allow")) {
                if(args.length == 1) {
                    Messages.ALLOW_CMD_USAGE.Send(p, true);
                    return true;
                }
                Entity ent = GetInSight(p, 10);
                
                ClaimingUtil.AddUser(p, ent, args[1]);


            } else if(args[0].equalsIgnoreCase("deny")) {
                //todo: finish allowing/denying players feature
                if(args.length == 1) {
                    Messages.DENY_CMD_USAGE.Send(p, true);
                    return true;
                }
                Entity ent = GetInSight(p, 10);
            
                if (ent == null) {
                    Messages.NO_ENTITY_FOUND.Send(p, true);
                    return true;
                }

                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                String data = persistentData.get(new NamespacedKey(self, "allowed"), PersistentDataType.STRING);
                ArrayList<String> playerList = new ArrayList<String>();
                if(data != null) {
                    playerList = new ArrayList<>(Arrays.asList(data.split(",")));
                }

                MojangUtil request = new MojangUtil();
                MojangUser denied = request.resolveUser(args[1]);

                if(denied == null) {
                    Messages.PLAYER_NOT_EXISTS.Send(p, true, new HashMap<String, String>() {
                        private static final long serialVersionUID = 1L;
                        {
                            put("Player", args[1]);
                        }
                    });
                    return true;
                } else if(!playerList.contains(denied.getUUID().toString())) {
                    Messages.DENY_CANT_REMOVE.Send(p, true);
                } else {
                    playerList.remove(denied.getUUID().toString());
                    String listString = String.join(",", playerList);

                    persistentData.set(new NamespacedKey(self, "allowed"), PersistentDataType.STRING, listString);
                    Messages.REMOVED_PLAYER.Send(p, true, new HashMap<String, String>() {
                        private static final long serialVersionUID = 1L;
                        {
                            put("Player", args[1]);
                        }
                    });
                }

            } else if(args[0].equalsIgnoreCase("list")) {
                //todo: add member list feature
                Entity ent = GetInSight(p, 10);
                
                if(!ClaimingUtil.IsOwner(p.getUniqueId(), ent)) {
                    Messages.ENTITY_NOT_YOURS.Send(p, true);
                    return true;
                }

                ArrayList<OfflinePlayer> players = ClaimingUtil.GetMembers(p, ent); 

                if(players.size() < 1) {
                    Messages.ENTITY_MEMBERLIST_EMPTY.Send(p, true);
                    return true;
                }

                Messages.ENTITY_MEMBER_LIST.Send(p, true);

                for(OfflinePlayer offP : players) {
                    p.sendMessage("§7- §b" + offP.getName());
                }
                 
            } else if(args[0].equalsIgnoreCase("book")) {
                ItemStack claimTool = CustomItem.Create(Material.BOOK, "§8» §7Entity Claim Book §8«", 1, true, "§8This book allows you to", "§8protect all sorts of entities!");
                ItemMeta meta = claimTool.getItemMeta();

                NamespacedKey key = new NamespacedKey(StickyTamer.GetPlugin(), "is-tool");
                meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
                claimTool.setItemMeta(meta);

                if(p.getInventory().firstEmpty() < 0) {
                    Messages.INVENTORY_FULL.Send(p, true);
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.75f, 1f);
                    return true;
                }

                p.getInventory().addItem(claimTool);
                Messages.CLAIM_TOOL_ADDED.Send(p, true);
                p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.75f, 1f);
            }

        }

        return true;
    }

    public Entity GetInSight(Player p, int range) {

        Vector playerLookDir = p.getEyeLocation().getDirection();
        Vector playerEyeLocation = p.getEyeLocation().toVector();

        Entity bestEntity = null;
        float bestAngle = 0.4f;

        for (Entity e : p.getNearbyEntities(range, range, range)) {
            if (!p.hasLineOfSight(e))
                continue;

            Vector entityLoc = e.getLocation().toVector();
            Vector playerToEntity = entityLoc.subtract(playerEyeLocation);

            if (playerLookDir.angle(playerToEntity) < bestAngle) {
                bestAngle = playerLookDir.angle(playerToEntity);
                bestEntity = e;
            }

        }

        return bestEntity;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> options = new ArrayList<String>();
        if(command.getName().equals("stickytamer")) {
            if(args.length == 1) {
                if(!args[0].equals("")) {
                    for(String s : argumentPool) {
                        if(s.startsWith(args[0].toLowerCase())) {
                            options.add(s);
                        }
                    }
                } else {
                    options.addAll(Arrays.asList(argumentPool));
                }
            } if(args.length > 2) return null;
        }

        return options;
    }
    
}