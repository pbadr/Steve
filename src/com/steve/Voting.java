package com.steve;

import com.steve.game.Game;
import com.steve.game.GameManager;
import org.bukkit.Bukkit;

import java.util.*;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RESET;

public class Voting {
    private static final HashMap<String, Game> gameCodes = new HashMap<>();
    private static final HashMap<String, Integer> gameVotes = new HashMap<>();
    private static final HashMap<String, HashMap<String, Integer>> gameWorldVotes = new HashMap<>();


    public static Game getGame(String gameCode) {
        return gameCodes.get(gameCode);
    }

    public static boolean atLeastOneVotableGameSupportsThisAmountOfPlayers() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        for (String s : gameVotes.keySet()) {
            Game g = gameCodes.get(s);
            if (onlinePlayers >= g.minPlayers() || onlinePlayers <= g.maxPlayers()) {
                return true;
            }
        }

        return false;
    }

    public static int setupGameVotes() {
        List<Game> availableGames = new ArrayList<>();
        for (Game g : GameManager.getAllGames()) {
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            if (g.minPlayers() <= onlinePlayers && onlinePlayers <= g.maxPlayers()) {
                availableGames.add(g);
            }
        }

        // no available games? don't attempt to travel
        int availableGamesSize = availableGames.size();
        if (availableGamesSize == 0) {
            return 0;
        }

        Collections.shuffle(availableGames); // shuffle the options

        // pick first 3 games of the shuffled list, and put them up for voting
        for (int i = 0; i < Math.min(3, availableGamesSize); i++) {
            Game pickedGame = availableGames.get(i);
            String code = pickedGame.code();
            gameVotes.put(code, 0);
            gameCodes.put(code, pickedGame);

            List<String> availableWorlds = Arrays.asList(pickedGame.worlds());
            Collections.shuffle(availableWorlds);
            HashMap<String, Integer> gameWorldVotesEntry = new HashMap<>();
            for (int j = 0; j < Math.min(3, availableWorlds.size()); j++) {
                gameWorldVotesEntry.put(availableWorlds.get(j), 0);
            }
            gameWorldVotes.put(pickedGame.code(), gameWorldVotesEntry);

            Util.broadcast("Votable game #" + (i + 1) + ": " + GREEN + pickedGame.name() + RESET +
                    ", maps: " + String.join(", ", gameWorldVotes.get(pickedGame.code()).keySet()));
        }

        return availableGamesSize;
    }

    public static void incrementGameVote(String gameCode) {
        int newValue = gameVotes.get(gameCode) + 1;
        gameVotes.put(gameCode, newValue);
    }

    public static void incrementGameWorldVote(String gameCode, String worldName) {
        HashMap<String, Integer> worldVotes = gameWorldVotes.get(gameCode);
        int newValue = worldVotes.get(worldName) + 1;
        worldVotes.put(worldName, newValue);
        // gameWorldVotes.put(gameCode, worldVotes); // @todo shouldn't be necessary (object reference)
    }

    public static HashMap<String, Integer> getGameVotes() {
        return new HashMap<>(gameVotes); // clones the hashmap
    }

    public static HashMap<String, HashMap<String, Integer>> getGameWorldVotes() {
        return new HashMap<>(gameWorldVotes); // clones the hashmap
    }

    public static Game getHighestVotedGame() {
        int highestVotes = 0;
        List<String> highestVotedGames = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : gameVotes.entrySet()) {
            if (entry.getValue() > highestVotes) {
                highestVotedGames.clear();
                highestVotedGames.add(entry.getKey());
                highestVotes = entry.getValue();
            } else if (entry.getValue() == highestVotes) {
                highestVotedGames.add(entry.getKey());
            }
        }

        if (highestVotedGames.size() == 0) {
            Bukkit.getLogger().severe("Somehow there are 0 highest voted games (should be all of them)");
            return null;
        }

        if (highestVotedGames.size() == 1) { // can't be 0, instead it contains every entry
            Game highestVotedGame = gameCodes.get(highestVotedGames.get(0));
            Util.broadcast("Most voted game: " + highestVotedGame.name());
            return highestVotedGame;
        }

        int randomIndex = new Random().nextInt(highestVotedGames.size());
        Game randomlyPickedGame = gameCodes.get(highestVotedGames.get(randomIndex));
        Util.broadcast("Randomly picked game: " + randomlyPickedGame.name());
        return randomlyPickedGame;
    }

    public static String getHighestVotedGameWorld(String gameCode) {
        int highestVotes = 0;
        List<String> highestVotedWorlds = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : gameWorldVotes.get(gameCode).entrySet()) {
            if (entry.getValue() > highestVotes) {
                highestVotedWorlds.clear();
                highestVotedWorlds.add(entry.getKey());
                highestVotes = entry.getValue();
            } else if (entry.getValue() == highestVotes) {
                highestVotedWorlds.add(entry.getKey());
            }
        }

        if (highestVotedWorlds.size() == 0) {
            Bukkit.getLogger().severe("Somehow there are 0 highest voted worlds (should be all of them)");
            return null;
        }

        if (highestVotedWorlds.size() == 1) {
            String highestVotedWorld = highestVotedWorlds.get(0);
            Util.broadcast("Most voted world: " + highestVotedWorld);
            return highestVotedWorld;
        }

        int randomIndex = new Random().nextInt(highestVotedWorlds.size());
        String highestVotedWorld = highestVotedWorlds.get(randomIndex);
        Util.broadcast("Randomly picked world: " + highestVotedWorld);
        return highestVotedWorld;
    }

    public static void clearVotes() {
        gameCodes.clear();
        gameVotes.clear();
        gameWorldVotes.clear();
    }
}
