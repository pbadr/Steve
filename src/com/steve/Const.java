package com.steve;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Const {
    static final String playerDataPath = "playerdata/";
    static List<PlayerData> allPlayerData = new ArrayList<>();
    static final String worldsPath = "worlds/";

    static void readData() {
        try (Stream<Path> walk = Files.walk(Paths.get(playerDataPath))) {
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());

            for (String filename : result) {
                String json = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
                Gson gson = new Gson();

                allPlayerData.add(gson.fromJson(json, PlayerData.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getLogger().info("Read from playerdata");
    }

    static void writeData() {
        for (PlayerData pd : allPlayerData) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try(FileWriter writer = new FileWriter(playerDataPath + pd.uuid + ".json")) {
                gson.toJson(pd, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bukkit.getLogger().info("Wrote to playerdata");
    }
}
