package me.kokumaji.Tamer.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class CustomItem implements Listener {

    public enum LimiterType {
        MUSHROOM_STEW,
        ARROW;
    }

    private static final Enchantment[] randomAxeEnchant = {
            Enchantment.DAMAGE_ARTHROPODS,
            Enchantment.VANISHING_CURSE,
            Enchantment.DAMAGE_ALL,
            Enchantment.DURABILITY
    };

    private static final Enchantment[] randomSwordEnchant = {
            Enchantment.DAMAGE_ARTHROPODS,
            Enchantment.VANISHING_CURSE,
            Enchantment.DAMAGE_ALL,
            Enchantment.FIRE_ASPECT,
            Enchantment.SWEEPING_EDGE,
            Enchantment.DURABILITY
    };

    private static final Enchantment[] randomBowEnchant = {
            Enchantment.ARROW_DAMAGE,
            Enchantment.ARROW_FIRE,
            Enchantment.ARROW_KNOCKBACK,
            Enchantment.DURABILITY
    };

    public static ItemStack Create(Material itemType, String name, int amount, boolean hideFlags, String... lore) {
        ItemStack item = new ItemStack(itemType, 1);
        ItemMeta meta = item.getItemMeta();
        name = ChatColor.translateAlternateColorCodes('&', name);
        meta.setDisplayName(name);

        if (hideFlags) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        ArrayList<String> metaLore = new ArrayList<>();

        for (String loreComments : lore) {
            loreComments = ChatColor.translateAlternateColorCodes('&', loreComments);
            metaLore.add(loreComments);
        }

        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    public static Enchantment RandomEnchantment(Material pMaterial) {
        if(pMaterial.toString().endsWith("_AXE")) {
            int rnd = new Random().nextInt(randomAxeEnchant.length);
            return randomAxeEnchant[rnd];
        } else if(pMaterial.toString().endsWith("_SWORD")) {
            int rnd = new Random().nextInt(randomSwordEnchant.length);
            return randomSwordEnchant[rnd];
        } else if(pMaterial.equals(Material.BOW)) {
            int rnd = new Random().nextInt(randomBowEnchant.length);
            return randomBowEnchant[rnd];
        }

        return null;
    }


    public static ItemStack CreateSkull(String url, String name, int amount, String... lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        if (url.isEmpty())
            return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }

        headMeta.setDisplayName(name);

        ArrayList<String> metaLore = new ArrayList<>();

        for (String loreComments : lore) {
            loreComments = ChatColor.translateAlternateColorCodes('&', loreComments);
            metaLore.add(loreComments);
        }

        headMeta.setLore(metaLore);

        head.setItemMeta(headMeta);


        head.setItemMeta(headMeta);
        return head;
    } 

    public static ItemStack CreateSkull(UUID owner, String name, int amount, String... lore) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD, amount);
        
        SkullMeta isM = (SkullMeta) is.getItemMeta();
        isM.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        isM.setDisplayName(name);

        ArrayList<String> metaLore = new ArrayList<>();

        for (String loreComments : lore) {
            loreComments = ChatColor.translateAlternateColorCodes('&', loreComments);
            metaLore.add(loreComments);
        }

        isM.setLore(metaLore);

        is.setItemMeta(isM);
        
        return is;
    }

}