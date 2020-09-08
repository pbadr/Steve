package com.steve;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Const {
    static final String playersPath = "data/players.json";
    HashMap<String, JSONObject> playerData = new HashMap<String, JSONObject>();
    static final String statsPath = "data/stats.json";
    static final String worldsPath = "worlds/";

    static void loadData() {
        JSONParser parser = new JSONParser();

        try {
            FileReader fileReader = new FileReader(playersPath);
            JSONObject playerdata = (JSONObject) parser.parse(fileReader);
            fileReader.close();

            for

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
