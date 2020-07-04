package me.kokumaji.StickyTamer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.kokumaji.StickyTamer.Commands.MainCommand;
import me.kokumaji.StickyTamer.Listeners.EntityInteractListener;

public final class StickyTamer extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("stickytamer").setExecutor(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new EntityInteractListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
