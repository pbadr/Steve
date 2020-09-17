package com.steve;

import org.bukkit.*;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import static org.bukkit.ChatColor.*;

public class Util {
    static final String PLUGIN_PATH = "plugins/Steve.jar";
    static final String WORLDS_PATH = "worlds/";

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

    public static void broadcast(Object msg) {
        Bukkit.broadcastMessage(msg.toString());
    }

    public static void pluginIsBuilt(Timestamp timestamp) {
        broadcast(GREEN + timestamp.toLocalDateTime().toString() + BOLD + " PLUGIN REBUILT - /reload :)");
    }

}
