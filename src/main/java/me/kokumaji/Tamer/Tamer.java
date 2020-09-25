package me.kokumaji.Tamer;

import me.kokumaji.HibiscusAPI.api.translation.Translator;
import me.kokumaji.Tamer.Commands.MainCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.kokumaji.Tamer.Listeners.EntityInteractListener;
import me.kokumaji.Tamer.Objects.GUIs.GUIHandler;

import java.io.IOException;
import java.util.Objects;

public final class Tamer extends JavaPlugin {

    private static Translator translate;
    private static FileConfiguration config;

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(new EntityInteractListener(), this);
        this.saveDefaultConfig();
        config = this.getConfig();

        try {
            translate = new Translator(this, config, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(getCommand("Tamer")).setExecutor((CommandExecutor) new MainCommand());
        GUIHandler.RegisterGUIs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin GetPlugin() {
		return getPlugin(Tamer.class);
    }

    public static Translator GetTranslator() {
        return translate;
    }

    public static FileConfiguration GetConfig() {
        return config;
    }
}
