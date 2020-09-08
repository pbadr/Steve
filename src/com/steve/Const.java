package com.steve;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Const {
    static final String dataPath = "data.json";
    static HashMap<String, HashMap<String, Object>> playerData = new HashMap<>();
    static final String worldsPath = "worlds/";

    static void readData() {
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

            Bukkit.getLogger().info("Read from " + dataPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void writeData() {
        Gson gson = new Gson();
        HashMap<String, Object> data = new HashMap<>();
        data.put("players", playerData);
        String toWrite = gson.toJson(data);
        System.out.println(toWrite);

        try {
            FileWriter fileWriter = new FileWriter("data.json");
            fileWriter.write(toWrite);
            fileWriter.close();
            Bukkit.getLogger().info("Wrote to " + dataPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void editPlayerData(String username, String key, Object value) {
        playerData.get(username).put(key, value);
    }

    static void incrementPlayerData(String username, String key) {
        long oldValue = (long) playerData.get(username).get(key);
        playerData.get(username).put(key, oldValue + 1);
    }

    static void incrementPlayerData(String username, String key, long incrementAmount) {
        long oldValue = (long) playerData.get(username).get(key);
        long newValue = oldValue + incrementAmount;
        playerData.get(username).put(key, newValue);
    }

    static void resetPlayerData(String username) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("serverJoins", 0);
        data.put("serverJoinedDate", null);
        data.put("kills", 0);
        data.put("deaths", 0);
        data.put("gamesWon", 0);
        data.put("gamesLost", 0);
        data.put("gamesPlayed", 0);
        data.put("messagesSent", 0);
        data.put("friendsAdded", new ArrayList<String>());
        data.put("playersBlocked", new ArrayList<String>());
        data.put("gameTypesPlayed", new HashMap<String, Integer>());
        data.put("gameTypesWon", new HashMap<String, Integer>());
        data.put("gameTypesLost", new HashMap<String, Integer>());
        playerData.put(username, data);
    }
}
