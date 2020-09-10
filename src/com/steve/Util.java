package com.steve;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Util {
    static final String PLUGINS_PATH = "plugins/Steve.jar";
    // static final String worldsPath = "worlds/";

    private static final LinkedHashMap<Integer, ChatColor> winsColors;
    static {
        winsColors = new LinkedHashMap<>();
        winsColors.put(0, ChatColor.DARK_GRAY);
        winsColors.put(5, ChatColor.GRAY);
        winsColors.put(10, ChatColor.WHITE);
        winsColors.put(15, ChatColor.YELLOW);
        winsColors.put(20, ChatColor.GOLD);
        winsColors.put(25, ChatColor.GREEN);
        winsColors.put(30, ChatColor.DARK_GREEN);
        winsColors.put(40, ChatColor.AQUA);
        winsColors.put(50, ChatColor.DARK_AQUA);
        winsColors.put(60, ChatColor.BLUE);
        winsColors.put(70, ChatColor.LIGHT_PURPLE);
        winsColors.put(80, ChatColor.DARK_PURPLE);
        winsColors.put(90, ChatColor.RED);
        winsColors.put(100, ChatColor.DARK_RED);
    }

    private static final String formatChar = "&";
    private static final HashMap<String, ChatColor> formatColors;
    static {
        formatColors = new HashMap<>();
        formatColors.put("R", ChatColor.DARK_RED);
        formatColors.put("r", ChatColor.RED);
        formatColors.put("Y", ChatColor.GOLD);
        formatColors.put("y", ChatColor.YELLOW);
        formatColors.put("G", ChatColor.DARK_GREEN);
        formatColors.put("g", ChatColor.GREEN);
        formatColors.put("a", ChatColor.AQUA);
        formatColors.put("A", ChatColor.DARK_AQUA);
        formatColors.put("B", ChatColor.DARK_BLUE);
        formatColors.put("b", ChatColor.BLUE);
        formatColors.put("P", ChatColor.LIGHT_PURPLE);
        formatColors.put("p", ChatColor.DARK_PURPLE);
        formatColors.put("w", ChatColor.WHITE);
        formatColors.put("o", ChatColor.GRAY);
        formatColors.put("O", ChatColor.DARK_GRAY);
        formatColors.put("0", ChatColor.BLACK);

        formatColors.put("d", ChatColor.BOLD);
        formatColors.put("i", ChatColor.ITALIC);
        formatColors.put("m", ChatColor.MAGIC);
        formatColors.put("u", ChatColor.UNDERLINE);
        formatColors.put("s", ChatColor.STRIKETHROUGH);
        formatColors.put("t", ChatColor.RESET);
    }

    public static ChatColor getWinsColor(int gamesWon) {
        ChatColor highestColor = winsColors.get(0);

        for (Integer minWins : winsColors.keySet()) {
            if (minWins == 0) continue;
            if (gamesWon >= minWins) highestColor = winsColors.get(minWins);
        }

        return highestColor;
    }

    public static void serverBroadcast(String msg) {
        broadcast("&r[Server]&t " + msg);
    }

    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(format(msg));
    }

    public static void pluginIsBuilt() {
        serverBroadcast("&g&dPLUGIN REBUILT!");
    }

    public static String format(String msg) {
        for (Map.Entry<String, ChatColor> entry : formatColors.entrySet()) {
            msg = msg.replaceAll(formatChar + entry.getKey(), "" + entry.getValue());
        }

        return msg;
    }
}
