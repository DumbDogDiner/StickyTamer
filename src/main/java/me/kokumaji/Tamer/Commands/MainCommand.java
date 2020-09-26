package me.kokumaji.Tamer.Commands;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;

import me.kokumaji.HibiscusAPI.api.translation.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import me.kokumaji.Tamer.Tamer;
import me.kokumaji.Tamer.Util.ClaimingUtil;
import me.kokumaji.Tamer.Util.CustomItem;
import me.kokumaji.Tamer.Util.Messages;
import me.kokumaji.Tamer.Util.MojangUtil;
import me.kokumaji.Tamer.Util.MojangUtil.MojangUser;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Tamer self = (Tamer) Tamer.GetPlugin();
    private final Translator translator = Tamer.GetTranslator();
    private final FileConfiguration config = Tamer.GetConfig();

    private final String[] argumentPool = {
        "info",
        "claim",
        "unclaim",
        "allow",
        "deny",
        "list",
        "book"
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length < 1) {
                String msg = config.getString("general-settings.plugin-prefix") + " {PluginName} {PluginVersion} developed by {PluginAuthor}";
                Messages.Send(p, msg, new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put("PluginName", self.getDescription().getName());
                        put("PluginVersion", self.getDescription().getVersion());
                        put("PluginAuthor", self.getDescription().getAuthors().get(0));
                    }
                });
            } else if (args[0].equalsIgnoreCase("info")) {
                if (!p.hasPermission("Tamer.info")) {
                    Messages.Send(p, translator.Translate("general.insufficient-permissions", true));
                    return true;
                }
                Entity ent = GetInSight(p, 10);
                if (ent == null) {
                    Messages.Send(p, translator.Translate("entity.no-entity-found", true));
                    return true;
                }

                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                String data = persistentData.get(CustomItem.GetKey("tamer"), PersistentDataType.STRING);
                if (data == null) {
                    Messages.Send(p, translator.Translate("entity.entity-unclaimed", true));
                    return true;
                }
                Messages.Send(p, translator.Translate("entity.entity-owner", true), new HashMap<String, String>() {
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
                    Messages.Send(p, translator.Translate("entity.no-entity-found", true));
                    return true;
                }

                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                String data = persistentData.get(CustomItem.GetKey("tamer"), PersistentDataType.STRING);
                if (data == null) {
                    Messages.Send(p, translator.Translate("entity.entity-unclaimed", true));
                    return true;
                }

                UUID dataUUID = UUID.fromString(data);

                System.out.println(dataUUID);
                System.out.println(p.getUniqueId());
                System.out.println(p.getUniqueId().equals(dataUUID));

                if(p.getUniqueId().equals(dataUUID)) {
                    Messages.Send(p, translator.Translate("entity.entity-cleared-claim", true));
                    persistentData.remove(CustomItem.GetKey("tamer"));
                    persistentData.remove(CustomItem.GetKey("allowed"));
                } else {
                    Messages.Send(p, translator.Translate("entity.entity-not-yours", true));
                }
            } else if(args[0].equalsIgnoreCase("allow")) {
                if(args.length == 1) {
                    //TODO: REPLACE THIS
                    //Messages.ALLOW_CMD_USAGE.Send(p, true);
                    return true;
                }
                Entity ent = GetInSight(p, 10);

                ClaimingUtil.AddUser(p, ent, args[1]);


            } else if(args[0].equalsIgnoreCase("deny")) {
                if(args.length == 1) {
                    //TODO: REPLACE THIS
                    //Messages.DENY_CMD_USAGE.Send(p, true);
                    return true;
                }
                Entity ent = GetInSight(p, 10);

                if (ent == null) {
                    Messages.Send(p, translator.Translate("entity.no-entity-found", true));
                    return true;
                }

                PersistentDataContainer persistentData = ent.getPersistentDataContainer();
                String data = persistentData.get(CustomItem.GetKey("allowed"), PersistentDataType.STRING);
                ArrayList<String> playerList = new ArrayList<String>();
                if(data != null) {
                    playerList = new ArrayList<>(Arrays.asList(data.split(",")));
                }

                MojangUtil request = new MojangUtil();
                MojangUser denied = request.resolveUser(args[1]);

                if(denied == null) {
                    Messages.Send(p, translator.Translate("command.player-not-exists", true), new HashMap<String, String>() {
                        private static final long serialVersionUID = 1L;

                        {
                            put("Player", args[1]);
                        }
                    });
                    return true;
                } else if(!playerList.contains(denied.getUUID().toString())) {
                    Messages.Send(p, translator.Translate("command.deny-cant-remove", true));
                } else {
                    playerList.remove(denied.getUUID().toString());
                    String listString = String.join(",", playerList);

                    persistentData.set(CustomItem.GetKey("allowed"), PersistentDataType.STRING, listString);
                    Messages.Send(p, translator.Translate("command.deny-removed-player", true), new HashMap<String, String>() {
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
                    Messages.Send(p, translator.Translate("entity.entity-not-yours", true));
                    return true;
                }

                ArrayList<OfflinePlayer> players = ClaimingUtil.GetMembers(p, ent);

                if(players.size() < 1) {
                    Messages.Send(p, translator.Translate("command.allowed-players-empty", true));
                    return true;
                }

                Messages.Send(p, translator.Translate("command.allowed-players", true));

                for(OfflinePlayer offP : players) {
                    p.sendMessage("§7- §b" + offP.getName());
                }

            } else if(args[0].equalsIgnoreCase("book")) {
                ItemStack claimTool = CustomItem.Create(Material.BOOK, "§8» §7Entity Claim Book §8«", 1, true, "§8This book allows you to", "§8protect all sorts of entities!");
                ItemMeta meta = claimTool.getItemMeta();

                NamespacedKey key = CustomItem.GetKey("is-tool");
                meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
                claimTool.setItemMeta(meta);

                if(p.getInventory().firstEmpty() < 0) {
                    Messages.Send(p, translator.Translate("general.full-inventory", true));
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.75f, 1f);
                    return true;
                }

                p.getInventory().addItem(claimTool);
                Messages.Send(p, translator.Translate("command.claim-tool-added", true));
                p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.75f, 1f);
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> options = new ArrayList<String>();
        if(command.getName().equals("Tamer")) {
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
}