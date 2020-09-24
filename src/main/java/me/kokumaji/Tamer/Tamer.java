package me.kokumaji.Tamer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.kokumaji.Tamer.Commands.MainCommand;
import me.kokumaji.Tamer.Listeners.EntityInteractListener;
import me.kokumaji.Tamer.Objects.GUIs.GUIHandler;

public final class Tamer extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("Tamer").setExecutor(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new EntityInteractListener(), this);
        GUIHandler.RegisterGUIs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin GetPlugin() {
		return getPlugin(Tamer.class);
    }
}
