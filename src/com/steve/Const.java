package com.steve;

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
                data.put("wins", (long) json.get("wins"));

                playerData.put((String) json.get("name"), data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
