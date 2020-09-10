package com.steve;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

public class Util {
    static final String PLUGINS_PATH = "plugins/Steve.jar";
    // static final String worldsPath = "worlds/";

    private static final LinkedHashMap<Integer, ChatColor> winsChatColors;
    static {
        winsChatColors = new LinkedHashMap<>();
        winsChatColors.put(0, ChatColor.DARK_GRAY);
        winsChatColors.put(5, ChatColor.GRAY);
        winsChatColors.put(10, ChatColor.WHITE);
        winsChatColors.put(15, ChatColor.YELLOW);
        winsChatColors.put(20, ChatColor.GOLD);
        winsChatColors.put(25, ChatColor.GREEN);
        winsChatColors.put(30, ChatColor.DARK_GREEN);
        winsChatColors.put(40, ChatColor.AQUA);
        winsChatColors.put(50, ChatColor.DARK_AQUA);
        winsChatColors.put(60, ChatColor.BLUE);
        winsChatColors.put(70, ChatColor.LIGHT_PURPLE);
        winsChatColors.put(80, ChatColor.DARK_PURPLE);
        winsChatColors.put(90, ChatColor.RED);
        winsChatColors.put(100, ChatColor.DARK_RED);
    }

    public static ChatColor getWinsColor(int gamesWon) {
        ChatColor highestColor = winsChatColors.get(0);

        for (Integer minWins : winsChatColors.keySet()) {
            if (minWins == 0) continue;
            if (gamesWon >= minWins) highestColor = winsChatColors.get(minWins);
        }

        return highestColor;
    }

    public static void pluginIsBuilt() {
        Bukkit.broadcastMessage(String.format("%s%sPLUGIN IS BUILT", ChatColor.BOLD, ChatColor.GREEN));
    }

    public static void reflectSet(Object object, String fieldName, Object fieldValue) {
        // by sp00m, source: https://stackoverflow.com/a/14374995/13216113

        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
