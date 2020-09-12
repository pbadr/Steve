package com.steve;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.steve.GameState.*;
import static org.bukkit.ChatColor.*;

public class Util {
    static final String PLUGINS_PATH = "plugins/Steve.jar";
    // static final String worldsPath = "worlds/";

    static int preparingTaskInt;
    public static void attemptPreparingTimer() {
        if (Bukkit.getOnlinePlayers().size() >= 2) {
            Main.gameState = PREPARING;

            preparingTaskInt = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                int t = 3;

                @Override
                public void run() {
                    if (Bukkit.getOnlinePlayers().size() < 2) {
                        Bukkit.getScheduler().cancelTask(preparingTaskInt);
                        steveBroadcast(RED + "Not enough players!");
                    } else if (t == 0) {
                        Bukkit.getScheduler().cancelTask(preparingTaskInt);
                        starting();
                    } else {
                        steveBroadcast(BLUE + "Preparing... " + t);
                        t -= 1;
                    }
                }

            }, 0, 20);
        }
    }

    static int startingTaskInt;
    public static void starting() {
        Main.gameState = STARTING;

        startingTaskInt = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            int t = 3;

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() < 2) {
                    Bukkit.getScheduler().cancelTask(startingTaskInt);
                    steveBroadcast(RED + "Not enough players!");
                } else if (t == 0) {
                    Bukkit.getScheduler().cancelTask(startingTaskInt);
                    start();
                } else {
                    steveBroadcast(AQUA + "Starting... " + t);
                    t -= 1;
                }
            }

        }, 0, 20);
    }

    public static void start() {
        Main.gameState = RUNNING;
        steveBroadcast(GREEN + "STARTED");
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player p : onlinePlayers) {
            // ..
            p.setGameMode(GameMode.ADVENTURE);
        }
    }

    static final HashMap<Player, Integer> playerExplodeTasks = new HashMap<>();
    public static void explodePlayerTask(Player p) {
        int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
            playerExplodeTasks.remove(p);
            p.damage(p.getHealth());

            World w = p.getWorld();
            Location pos = p.getLocation();

            w.spawnParticle(Particle.CLOUD, pos, 10);
            // w.createExplosion(pos, 4f);

        }, 100);
        playerExplodeTasks.put(p, task);

        Util.steveBroadcast(p.getName() + " is about to explode!");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
        p.getInventory().setHelmet(new ItemStack(Material.TNT));
        System.out.println(playerExplodeTasks.keySet().toString());
    }

    public static void createFakePlatform(Location pos) {
        int size = 5;

        for (int xOffset = 0; xOffset < size; xOffset++) {
            for (int zOffset = 0; zOffset < size; zOffset++) {
                Block b = pos.getBlock().getRelative(xOffset, 0, zOffset);
                b.setType(Material.GOLD_BLOCK);
                b.setMetadata("isFake", new FixedMetadataValue(Main.plugin, true));
            }
        }
    }

    private static final LinkedHashMap<Integer, ChatColor> winsColors;
    static {
        winsColors = new LinkedHashMap<>();
        winsColors.put(0, DARK_GRAY);
        winsColors.put(5, GRAY);
        winsColors.put(10, WHITE);
        winsColors.put(15, YELLOW);
        winsColors.put(20, GOLD);
        winsColors.put(25, GREEN);
        winsColors.put(30, DARK_GREEN);
        winsColors.put(40, AQUA);
        winsColors.put(50, DARK_AQUA);
        winsColors.put(60, BLUE);
        winsColors.put(70, LIGHT_PURPLE);
        winsColors.put(80, DARK_PURPLE);
        winsColors.put(90, RED);
        winsColors.put(100, DARK_RED);
    }

    public static ChatColor getWinsColor(int gamesWon) {
        ChatColor highestColor = winsColors.get(0);

        for (Integer minWins : winsColors.keySet()) {
            if (minWins == 0) continue;
            if (gamesWon >= minWins) highestColor = winsColors.get(minWins);
        }

        return highestColor;
    }

    public static void steveBroadcast(Object msg) {
        broadcast(RED + "[Steve] " + RESET + msg);
    }

    public static void broadcast(Object msg) {
        Bukkit.broadcastMessage(msg.toString());
    }

    public static void pluginIsBuilt() {
        steveBroadcast("&g&dPLUGIN REBUILT - do /reload :)");
    }

}
