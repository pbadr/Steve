package com.steve;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerData {
    private static final String PATH = "playerdata/";
    private static final ArrayList<PlayerData> ALL_DATA = new ArrayList<>();

    public String name;
    public String uuid;
    public int serverJoins;
    public long firstJoinTimestamp;
    public long lastOnlineTimestamp;
    public int kills;
    public int deaths;
    public int gamesWon;
    public int gamesLost;
    public int gamesPlayed;
    public List<String> friendsAdded;
    public List<String> playersBlocked;
    public HashMap<String, Integer> gameTypesWon;
    public HashMap<String, Integer> gameTypesLost;
    public HashMap<String, Integer> gameTypesPlayed;

    public PlayerData(String name, UUID uuid, long currentTime) {
        this.name = name;
        this.uuid = uuid.toString();
        this.serverJoins = 0;
        this.firstJoinTimestamp = currentTime;
        this.lastOnlineTimestamp = currentTime;
        this.kills = 0;
        this.deaths = 0;
        this.gamesWon = 0;
        this.gamesLost = 0;
        this.gamesPlayed = 0;
        this.friendsAdded = new ArrayList<>();
        this.playersBlocked = new ArrayList<>();
        this.gameTypesWon = new HashMap<>();
        this.gameTypesLost = new HashMap<>();
        this.gameTypesPlayed = new HashMap<>();
    }

    public static PlayerData get(UUID uuid) {
        for (PlayerData pd : ALL_DATA) {
            if (pd.uuid.equals(uuid.toString())) {
                return pd;
            }
        }

        Bukkit.getLogger().warning("Invalid UUID");
        return new PlayerData("ERROR", UUID.fromString(""), 0); // @todo cleanup
    }

    public static void register(String name, UUID uuid, long currentTime) {
        ALL_DATA.add(new PlayerData(name, uuid, currentTime));
    }

    public static boolean exists(UUID uuid) {
        for (PlayerData pd : ALL_DATA) {
            if (pd.uuid.equals(uuid.toString())) {
                return true;
            }
        }

        return false;
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
