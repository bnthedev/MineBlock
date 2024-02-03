package me.bnthedev.Commands;

import me.bnthedev.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class MineBlockCommand implements CommandExecutor {
    private final Main plugin;


    public MineBlockCommand(Plugin plugin) {
        this.plugin = (Main) plugin;

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                plugin.getConfig().set("mineblocks." + args[1] + ".location.x", p.getLocation().getBlockX());
                plugin.getConfig().set("mineblocks." + args[1] + ".location.y", p.getLocation().getBlockY());
                plugin.getConfig().set("mineblocks." + args[1] + ".location.z", p.getLocation().getBlockZ());
                plugin.getConfig().set("mineblocks." + args[1] + ".location.world", p.getLocation().getWorld().getName());
                plugin.getConfig().set("mineblocks." + args[1] + ".points", 100);
                plugin.getConfig().set("mineblocks." + args[1] + ".defaultpoints", 100);

                p.sendMessage(plugin.getMessage("messages.mineblockset", args[1]));

                plugin.saveConfig();
            }
        }
        return false;
    }

}
