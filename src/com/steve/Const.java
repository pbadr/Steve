package com.steve;

import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.HashMap;

public class Const {
    static final String dataPath = "data.json";
    static HashMap<String, HashMap<String, Object>> playerData = new HashMap<>();
    static final String worldsPath = "worlds/";

    static void loadData() {
        JSONParser parser = new JSONParser();

        try {
            FileReader fileReader = new FileReader(dataPath);
            JSONObject dataObject = (JSONObject) parser.parse(fileReader);
            fileReader.close();

            JSONArray playersObject = (JSONArray) dataObject.get("players");

            for (Object playerObject : playersObject) {
                JSONObject json = (JSONObject) playerObject;

                HashMap<String, Object> data = new HashMap<>();
                data.put("wins", json.get("wins"));

                playerData.put((String) json.get("name"), data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void editPlayerData(String username, String key, Object value) {
        playerData.get(username).put(key, value);
    }

    static void incrementPlayerData(String username, String key) {

    }

    static void incrementPlayerData(String username, String key, long amount) {
        long oldValue = (long) playerData.get(username).get(key);
        long newValue = oldValue + amount;
        playerData.get(username).put(key, newValue);
    }
}
