package com.steve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    String name;
    String uuid;
    int serverJoins;
    long firstJoinTimestamp;
    long lastOnlineTimestamp;
    int kills;
    int deaths;
    int gamesWon;
    int gamesLost;
    int gamesPlayed;
    List<String> friendsAdded;
    List<String> playersBlocked;
    HashMap<String, Integer> gameTypesWon;
    HashMap<String, Integer> gameTypesLost;
    HashMap<String, Integer> gameTypesPlayed;

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

    static PlayerData get(UUID uuid) {
        for (PlayerData pd : Const.allPlayerData) {
            if (pd.uuid.equals(uuid.toString())) {
                return pd;
            }
        }

        return new PlayerData("ERROR", UUID.fromString(""), 0); // @todo cleanup
    }

    static boolean exists(UUID uuid) {
        for (PlayerData pd : Const.allPlayerData) {
            if (pd.uuid.equals(uuid.toString())) {
                return true;
            }
        }

        return false;
    }

}
