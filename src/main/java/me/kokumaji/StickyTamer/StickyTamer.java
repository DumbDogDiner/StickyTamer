package me.kokumaji.StickyTamer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.kokumaji.StickyTamer.Commands.MainCommand;
import me.kokumaji.StickyTamer.Listeners.EntityInteractListener;
import me.kokumaji.StickyTamer.Objects.GUIs.GUIHandler;

public final class StickyTamer extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("stickytamer").setExecutor(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new EntityInteractListener(), this);
        GUIHandler.RegisterGUIs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin GetPlugin() {
		return getPlugin(StickyTamer.class);
    }
}
