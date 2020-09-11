package com.steve;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class Util {
    static final String PLUGINS_PATH = "plugins/Steve.jar";
    // static final String worldsPath = "worlds/";

    static final HashMap<Player, Integer> playerExplodeTask = new HashMap<>();

    public static void explodePlayerTask(Player p) {
        int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.main, () -> {
            playerExplodeTask.remove(p);
            p.damage(p.getHealth());

            World w = p.getWorld();
            Location pos = p.getLocation();

            w.spawnParticle(Particle.CLOUD, pos, 10);
            // w.createExplosion(pos, 4f);

        }, 100);
        playerExplodeTask.put(p, task);

        Util.steveBroadcast(p.getName() + " is about to explode!");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
        p.getInventory().setHelmet(new ItemStack(Material.TNT));
        System.out.println(playerExplodeTask.keySet().toString());
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

    private static final String formatChar = "&";
    private static final HashMap<String, ChatColor> formatColors;
    static {
        formatColors = new HashMap<>();
        formatColors.put("R", DARK_RED);
        formatColors.put("r", RED);
        formatColors.put("Y", GOLD);
        formatColors.put("y", YELLOW);
        formatColors.put("G", DARK_GREEN);
        formatColors.put("g", GREEN);
        formatColors.put("a", AQUA);
        formatColors.put("A", DARK_AQUA);
        formatColors.put("B", DARK_BLUE);
        formatColors.put("b", BLUE);
        formatColors.put("P", LIGHT_PURPLE);
        formatColors.put("p", DARK_PURPLE);
        formatColors.put("w", WHITE);
        formatColors.put("o", GRAY);
        formatColors.put("O", DARK_GRAY);
        formatColors.put("0", BLACK);

        formatColors.put("d", BOLD);
        formatColors.put("i", ITALIC);
        formatColors.put("m", MAGIC);
        formatColors.put("u", UNDERLINE);
        formatColors.put("s", STRIKETHROUGH);
        formatColors.put("t", RESET);
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
        broadcast("&r[Steve]&t " + msg);
    }

    public static void broadcast(Object msg) {
        Bukkit.broadcastMessage(format(msg.toString()));
    }

    public static void pluginIsBuilt() {
        steveBroadcast("&g&dPLUGIN REBUILT!");
    }

    public static String format(String msg) {
        for (Map.Entry<String, ChatColor> entry : formatColors.entrySet()) {
            msg = msg.replaceAll(formatChar + entry.getKey(), "" + entry.getValue());
        }

        return msg;
    }
}
