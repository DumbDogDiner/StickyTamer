package me.kokumaji.StickyTamer.Util;

import java.util.HashMap;

import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Messages {
    ENTITY_OWNER("&7Entity Owner of &b{Entity}&7: \n&b{OwnerName} ({OwnerUUID})"), 
    ENTITY_ALREADY_CLAIMED("&7This entity already belongs to \n&b{OwnerName} ({OwnerUUID})"), 
    ENTITY_LINK_SUCCESS("&7Successfully claimed this entity!"), 
    INSUFFICIENT_PERMS("&cNot enough permssions to execute this command!"), 
    NO_ENTITY_FOUND("&7You have to look at an entity!"), 
    ENTITY_UNCLAIMED("&7Nobody has claimed this entity yet."),
    ENTITY_UNCLAIMED_TUTORIAL("&7Nobody has claimed this entity yet. &bSneak + Right Click to claim it!"), 
    ABOUT_PLUGIN("&b{PluginName} {PluginVersion} &7developed by &b{PluginAuthor}"), 
    ENTITY_NOT_YOURS("&7This entity does not belong to you!"), 
    ENITTY_CLEARED_CLAIM("&7Successfully unclaimed this entity!"), 
    ALLOW_CMD_USAGE("&7/stickytamer allow [Player]"), 
    ALLOW_CMD_ERROR_FULL_LIST("&7You can't add more than &b8 &7players!"), 
    PLAYER_NOT_EXISTS("&7Could not find player &b{Player}"), 
    CANT_ADD_SELF("&7You're the owner of this entity!"), 
    ADDED_PLAYER("&b{Player} &7can now interact with this entity."), 
    ENTITY_MEMBER_LIST("&7Allowed players:"), 
    DENY_CMD_USAGE("&7/stickytamer deny [Player]"), 
    DENY_CANT_REMOVE("&7Player already removed!"), 
    REMOVED_PLAYER("&b{Player} &7won't be able to interact with this entity anymore"), 
    ENTITY_MEMBERLIST_EMPTY("&7No other players are allowed to interact with this entity."), 
    CHILD_NOT_PROTECTED("&7Child spawned without protection, due to mother/father entity not being claimed by you."), 
    CHILD_PROTECTED("&7A child entity has been protected automatically during breeding."), 
    CANT_PROTECT("&7You cannot protect this entity."), 
    INVENTORY_FULL("&cYour inventory is full!"), 
    CLAIM_TOOL_ADDED("&7You received the &bEntity Claim Book&7! Right-click on an entity to claim it."), 
    OPEN_EDIT_MENU("&7Opening edit menu..."), 
    PLAYER_NEVER_JOINED("&b{Player} &7has never joined the server before.");
    
    private String msg;
    Messages(String str) {
        this.msg = str;
    }

	public void Send(Player p, boolean b, HashMap<String, String> values) {
        StrSubstitutor sub = new StrSubstitutor(values, "{", "}");
        String result = sub.replace(this.msg);
        if(b) {
            result = ChatColor.translateAlternateColorCodes('&', "&3&lTaming &8» " + result);
        } else {
            result = ChatColor.translateAlternateColorCodes('&', result);
        }
        
        p.sendMessage(result);
    }
    
    public void Send(Player p, boolean b) {
        String result = this.msg;
        if(b) {
            result = ChatColor.translateAlternateColorCodes('&', "&3&lTaming &8» " + result);
        } else {
            result = ChatColor.translateAlternateColorCodes('&', result);
        }
        
        p.sendMessage(result);
	}
}