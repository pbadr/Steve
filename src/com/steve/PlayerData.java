package com.steve;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerData {
    private static final String PATH = "playerdata/";
    private static final ArrayList<PlayerData> ALL_DATA = new ArrayList<>();

    public String name;
    public String uuid;

    public int deaths;
    public long registerTimestamp;
    public List<String> friendsAdded;
    public int gamesLost;
    public int gamesPlayed;
    public int gamesWon;
    public HashMap<String, Integer> gameTypesLost;
    public HashMap<String, Integer> gameTypesPlayed;
    public HashMap<String, Integer> gameTypesWon;
    public int kills;
    public long lastLoginTimestamp;
    public long lastLogoutTimestamp;
    public int messagesSent;
    public List<String> playersBlocked;
    public int serverJoins;

    private PlayerData(Player player) {
        this.name = player.getName();
        this.uuid = player.getUniqueId().toString();

        this.deaths = 0;
        this.registerTimestamp = System.currentTimeMillis();
        this.friendsAdded = new ArrayList<>();
        this.gamesLost = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.gameTypesLost = new HashMap<>();
        this.gameTypesPlayed = new HashMap<>();
        this.gameTypesWon = new HashMap<>();
        this.kills = 0;
        this.lastLoginTimestamp = 0;
        this.lastLogoutTimestamp = 0;
        this.messagesSent = 0;
        this.playersBlocked = new ArrayList<>();
        this.serverJoins = 0;

        for (String gameName : new String[] {"tiptoe"}) {
            gameTypesLost.put(gameName, 0);
            gameTypesPlayed.put(gameName, 0);
            gameTypesWon.put(gameName, 0);
        }
    }

    public static PlayerData get(Player player) {
        for (PlayerData pd : ALL_DATA) {
            if (pd.uuid.equals(player.getUniqueId().toString())) {
                return pd;
            }
        }

        Bukkit.getLogger().severe("Invalid UUID passed to PlayerData.get(): " + player.getUniqueId());
        return new PlayerData(player); // @todo check if this prevents crash
    }

    public void incrementGameStat(String game, String incrementWhat) {
        switch (incrementWhat) {
            case "lost":
                gameTypesLost.put(game, gameTypesLost.get(game) + 1);
                return;
            case "played":
                gameTypesPlayed.put(game, gameTypesPlayed.get(game) + 1);
                return;
            case "won":
                gameTypesWon.put(game, gameTypesWon.get(game) + 1);
        }
    }

    public static void registerIfNotExisting(Player player) {
        if (exists(player)) return;

        ALL_DATA.add(new PlayerData(player));
        Bukkit.getLogger().info("Registered PlayerData for " + player.getName());
    }

    public static boolean exists(Player player) {
        for (PlayerData pd : ALL_DATA) {
            if (pd.uuid.equals(player.getUniqueId().toString())) {
                return true;
            }
        }

        return false;
    }

    public static void emptyMemory() {
        // deletes all playerdata in memory (ALL_DATA), and thus wiping all data if writeDisk() gets called!
        ALL_DATA.clear();
        Bukkit.getLogger().info("Emptied ALL_DATA! (if the plugin writes to disk, all data will be lost!)");
    }

    public static Object reflectSet(Object object, String fieldName, Object value) {
        // by sp00m, source: https://stackoverflow.com/a/14374995/13216113

        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, value);
                return value;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        return null;
    }

    public static void readDisk() {
        try (Stream<Path> walk = Files.walk(Paths.get(PATH))) {
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());

            for (String filename : result) {
                String json = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
                Gson gson = new Gson();

                // @todo if playerdata json file is outdated, properties could be null, causing NPEs
                ALL_DATA.add(gson.fromJson(json, PlayerData.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getLogger().info("Read from playerdata");
    }

    public static void writeDisk() {
        for (PlayerData pd : ALL_DATA) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try(FileWriter writer = new FileWriter(PATH + pd.uuid + ".json")) {
                gson.toJson(pd, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bukkit.getLogger().info("Wrote to playerdata");
    }

}
