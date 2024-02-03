package me.bnthedev.Listeners;

import me.bnthedev.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class OnBlockBreak implements Listener {
    private final Main plugin;
    private final Map<Location, ArmorStand> holograms;
    private int countdownSeconds = 60;


    public OnBlockBreak(Plugin plugin) {
        this.plugin = (Main) plugin;
        this.holograms = new HashMap<>();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();

        if (isMineBlock(block.getLocation())) {
            event.setCancelled(true);

            String key = getMineBlock(block.getLocation());
            plugin.getConfig().set("mineblocks." + key + ".block", block.getBlockData().getMaterial());

            int remainingPoints = plugin.getConfig().getInt("mineblocks." + key + ".points");
            if (remainingPoints > 0) {
                plugin.getConfig().set("mineblocks." + key + ".points", remainingPoints - 1);

                if (plugin.getConfig().getInt("mineblocks." + key + ".playerpoints." + p.getName()) <= 0) {
                    plugin.getConfig().set("mineblocks." + key + ".playerpoints." + p.getName(), 1);
                } else {
                    plugin.getConfig().set("mineblocks." + key + ".playerpoints." + p.getName(),
                            plugin.getConfig().getInt("mineblocks." + key + ".playerpoints." + p.getName()) + 1);
                }

                updateHologram(block.getLocation(), remainingPoints - 1, key);
            } else {
                plugin.getConfig().set("mineblocks." + key + ".block", block.getType().name());
                for (String playerName : plugin.getConfig().getConfigurationSection("mineblocks." + key + ".playerpoints").getKeys(false)) {
                    int i = plugin.getConfig().getInt("mineblocks." + key + ".playerpoints." + playerName);
                    plugin.getConfig().set("mineblocks." + key + ".playerpoints." + playerName, 0);
                    Player player = Bukkit.getPlayerExact(playerName);
                    ItemStack rewardItem = new ItemStack(Material.SUNFLOWER, i);
                    player.getInventory().addItem(rewardItem);
                    player.sendMessage(plugin.getMessage("messages.reward", key));


                }
                plugin.getConfig().set("mineblocks." + key + ".playerpoints", null);

                block.setType(Material.BEDROCK);
                removeHologram(block.getLocation());
                startTask(block.getLocation(), key, block);

            }

            plugin.saveConfig();
        }
    }
    private void removeHologram(Location location) {
        if (holograms.containsKey(location)) {
            ArmorStand hologram = holograms.get(location);
            hologram.remove();
            holograms.remove(location);
        }
    }
    private void updateHologram(Location location, int remainingPoints, String mineBlockName) {
        if (holograms.containsKey(location)) {
            ArmorStand hologram = holograms.get(location);
            hologram.setCustomName(ChatColor.DARK_GREEN + "Mine Block: " +  ChatColor.GREEN + mineBlockName + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Zbývá vytěžení: " + ChatColor.GREEN +  remainingPoints);
        } else {
            Location hologramLocation = location.clone().add(0.5, 1.5, 0.5);
            ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);

            hologram.setCustomName(ChatColor.DARK_GREEN + "Mine Block: " +  ChatColor.GREEN + mineBlockName + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Zbývá vytěžení: " + ChatColor.GREEN +  remainingPoints);
            hologram.setCustomNameVisible(true);
            hologram.setGravity(false);
            hologram.setVisible(false);

            hologram.setMarker(true);
            hologram.setCollidable(false);

            holograms.put(location, hologram);
        }
    }
    private void updateHologram2(Location location, String mineBlockName, int seconds) {
        if (holograms.containsKey(location)) {
            ArmorStand hologram = holograms.get(location);
            hologram.setCustomName(ChatColor.DARK_GREEN + "Mine Block: " + ChatColor.GREEN + mineBlockName + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Čas do regenerace: " + ChatColor.GREEN +  seconds);
        } else {
            Location hologramLocation = location.clone().add(0.5, 1.5, 0.5);
            ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);

            hologram.setCustomName(ChatColor.DARK_GREEN + "Mine Block: " + ChatColor.GREEN + mineBlockName + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "Čas do regenerace: " + ChatColor.GREEN +  seconds);
            hologram.setCustomNameVisible(true);
            hologram.setGravity(false);
            hologram.setVisible(false);

            hologram.setMarker(true);
            hologram.setCollidable(false);

            holograms.put(location, hologram);
        }
    }



    private boolean isMineBlock(Location location) {
        for (String key : plugin.getConfig().getConfigurationSection("mineblocks").getKeys(false)) {
            int x = plugin.getConfig().getInt("mineblocks." + key + ".location.x");
            int y = plugin.getConfig().getInt("mineblocks." + key + ".location.y");
            int z = plugin.getConfig().getInt("mineblocks." + key + ".location.z");
            String worldName = plugin.getConfig().getString("mineblocks." + key + ".location.world");

            if (location.getX() == x && location.getY() == y && location.getZ() == z
                    && location.getWorld().getName().equals(worldName)) {
                return true;
            }
        }

        return false;
    }

    private String getMineBlock(Location location) {
        for (String key : plugin.getConfig().getConfigurationSection("mineblocks").getKeys(false)) {
            int x = plugin.getConfig().getInt("mineblocks." + key + ".location.x");
            int y = plugin.getConfig().getInt("mineblocks." + key + ".location.y");
            int z = plugin.getConfig().getInt("mineblocks." + key + ".location.z");
            String worldName = plugin.getConfig().getString("mineblocks." + key + ".location.world");

            if (location.getX() == x && location.getY() == y && location.getZ() == z
                    && location.getWorld().getName().equals(worldName)) {
                return key;
            }
        }

        return null;
    }

    private void startTask(Location location, String mineBlockName, Block block) {
        new BukkitRunnable() {
            private int secondsLeft = 60;

            @Override
            public void run() {
                if (secondsLeft > 0) {
                    updateHologram2(location, mineBlockName, secondsLeft);
                    secondsLeft--;
                } else {
                    removeHologram(location);
                    String savedBlockTypeName = plugin.getConfig().getString("mineblocks." + mineBlockName + ".block");

                    block.setType(Material.getMaterial(savedBlockTypeName));
                    plugin.getConfig().set("mineblocks." + mineBlockName + ".points", plugin.getConfig().getInt("mineblocks." + mineBlockName + ".defaultpoints"));
                    updateHologram(location, plugin.getConfig().getInt("mineblocks." + mineBlockName + ".defaultpoints"), mineBlockName);
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 20);
    }
}
