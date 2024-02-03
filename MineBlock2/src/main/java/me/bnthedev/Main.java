package me.bnthedev;

import me.bnthedev.Commands.MineBlockCommand;
import me.bnthedev.Listeners.OnBlockBreak;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private Main plugin;



    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("mineblock").setExecutor(new MineBlockCommand(this));
        getServer().getPluginManager().registerEvents(new OnBlockBreak(this), this);


    }

    @Override
    public void onDisable() {
    }
    public String getMessage(String key, String name) {
        return ChatColor.translateAlternateColorCodes('&',
                getConfig().getString(key)
                        .replace("%name%", name)
        );
    }

}
