package com.steve.game;

import com.steve.Main;
import com.steve.PlayerData;
import com.steve.Util;
import com.steve.WorldManager;
import com.steve.game.tiptoe.TipToeGame;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class GameManager {
    public static Game game;
    public static GameState state = LOBBY;

    private static final HashMap<String, Game> gameCodes = new HashMap<>();
    private static final HashMap<String, Integer> gameVotes = new HashMap<>();
    private static final HashMap<String, HashMap<String, Integer>> gameWorldVotes = new HashMap<>();

    public static final int travellingSeconds = 5;
    public static final int startingSeconds = 10;
    public static final int returningDelaySeconds = 2;

    static BukkitTask travellingTask;
    static BukkitTask startingTask;
    static BukkitTask returnToLobbyTask;

    private static boolean canChangeState(GameState changeTo) {
        boolean canChange = false;

        switch (changeTo) { // inspired by oc.tc PGM source code
            case LOBBY:
                canChange = state == ENDED;
                break;
            case TRAVELLING:
                canChange = state == LOBBY;
                break;
            case STARTING:
                canChange = state == TRAVELLING;
                break;
            case STARTED:
                canChange = state == STARTING;
                break;
            case ENDED:
                canChange = state == TRAVELLING || state == STARTING || state == STARTED;
                break;
        }

        if (canChange) {
            state = changeTo;
        } else {
            Util.broadcast(RED + "Can't change state from " + state + " to " + changeTo);
        }

        return canChange;
    }

    public static boolean atLeastOneGameSupportsThisAmountOfPlayers() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        for (String s : gameVotes.keySet()) {
            Game g = gameCodes.get(s);
            if (onlinePlayers >= g.getMinPlayers() || onlinePlayers <= g.getMaxPlayers()) {
                return true;
            }
        }

        return false;
    }

    public static boolean currentGamePlayerCountOk() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        return game != null && onlinePlayers >= game.getMinPlayers() && onlinePlayers <= game.getMaxPlayers();
    }

    public static List<Game> getAllGames() {
        List<Game> allGames = new ArrayList<>();
        // @todo set static class variable with strings as names instead of instancing new games every time
        allGames.add(new TipToeGame());

        return allGames;
    }

    public static int setupGameVotes() {
        List<Game> availableGames = new ArrayList<>();
        for (Game g : getAllGames()) {
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            if (g.getMinPlayers() <= onlinePlayers && onlinePlayers <= g.getMaxPlayers()) {
                availableGames.add(g);
            }
        }

        // no available games? don't attempt to travel
        int availableGameSize = availableGames.size();
        if (availableGameSize == 0) {
            return 0;
        }

        Collections.shuffle(availableGames); // shuffle the options

        // pick first 3 games of the shuffled list, and put them up for voting
        for (int i = 0; i < Math.min(3, availableGameSize); i++) {
            Game pickedGame = availableGames.get(i);
            String code = pickedGame.getCode();
            gameVotes.put(code, 0);
            gameCodes.put(code, pickedGame);

            HashMap<String, Integer> gameWorldVoteEntry = new HashMap<>();
            for (String supportedWorld : pickedGame.getSupportedWorlds()) {
                gameWorldVoteEntry.put(supportedWorld, 0);
            }
            gameWorldVotes.put(pickedGame.getCode(), gameWorldVoteEntry);

            Util.broadcast("Votable game #" + (i + 1) + ": " + GREEN + pickedGame.getName() + RESET +
                    ", maps: " + String.join(", ", pickedGame.getSupportedWorlds()));
        }

        return availableGameSize;
    }

    public static Game getGame(String gameCode) {
        return gameCodes.get(gameCode);
    }

    public static HashMap<String, Integer> getGameVotes() {
        return new HashMap<>(gameVotes); // clones the hashmap
    }

    public static HashMap<String, HashMap<String, Integer>> getGameWorldVotes() {
        return new HashMap<>(gameWorldVotes); // clones the hashmap
    }

    public static void incrementGameVote(String gameCode) {
        int old = gameVotes.get(gameCode);
        gameVotes.put(gameCode, old + 1);
    }

    public static Game getHighestVotedGame() {
        int highestVotes = 0;
        List<String> highestVotedGames = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : gameVotes.entrySet()) {
            if (entry.getValue() >= highestVotes) {
                highestVotedGames.add(entry.getKey());
            }
        }

        if (highestVotedGames.size() == 1) { // can't be 0, instead it contains every entry
            Game highestVotedGame = gameCodes.get(highestVotedGames.get(0));
            Util.broadcast("Most voted game: " + highestVotedGame.getName());
            return highestVotedGame;
        }

        int randomIndex = new Random().nextInt(highestVotedGames.size());
        Game randomlyPickedGame = gameCodes.get(highestVotedGames.get(randomIndex));
        Util.broadcast("Randomly picked game: " + randomlyPickedGame.getName());
        return randomlyPickedGame;
    }

    public static String getHighestVotedGameWorld(String gameCode) {
        int highestVotes = 0;
        List<String> highestVotedWorlds = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : gameWorldVotes.get(gameCode).entrySet()) {
            if (entry.getValue() >= highestVotes) {
                highestVotedWorlds.add(entry.getKey());
            }
        }

        if (highestVotedWorlds.size() == 1) { // can't be 0, instead it contains every entry
            String highestVotedWorld = highestVotedWorlds.get(0);
            Util.broadcast("Most voted world: " + highestVotedWorld);
            return highestVotedWorld;
        }

        int randomIndex = new Random().nextInt(highestVotedWorlds.size());
        String highestVotedWorld = highestVotedWorlds.get(randomIndex);
        Util.broadcast("Randomly picked world: " + highestVotedWorld);
        return highestVotedWorld;
    }

    public static void handleDisconnect(Player p) {
        handleDeath(p);
    }

    public static void handleDeath(Player p) {
        if (state != STARTED) {
            p.sendMessage("Game hasn't started yet, whoops");
            Bukkit.getLogger().info("Ignoring death of " + p.getName());
            return;
        }

        GameMode diedGm = p.getGameMode();
        if (diedGm != ADVENTURE && diedGm != SURVIVAL) {
            p.sendMessage("You can't even die, whoops");
            Bukkit.getLogger().info("Ignoring death of " + p.getName());
            return;
        }

        List<Player> alivePlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            GameMode onlineGm = onlinePlayer.getGameMode();
            if (onlineGm == ADVENTURE || onlineGm == SURVIVAL) {
                alivePlayers.add(onlinePlayer);
            }
        }

        p.getWorld().strikeLightningEffect(p.getLocation());
        p.setGameMode(SPECTATOR);
        Util.sendTitle(RED + "You died!", null, 10, 40, 10);
        PlayerData pd = PlayerData.get(p);
        pd.deaths += 1;

        if (alivePlayers.size() == 0) {
            end(null);
        } else if (alivePlayers.size() == 1) {
            Player pWinner = alivePlayers.get(0);
            end(pWinner);
        } else {
            Util.broadcast(p.getName() + " died, " + alivePlayers.size() + " remain");
        }
    }

    public static void cancelGameTasks() {
        // scheduler.isCurrentlyRunning only returns true if this method is called inside a task's @Override run()
        // method, so instead it's currently implemented a non-null value means the task is running

        if (travellingTask != null) {
            travellingTask.cancel();
            travellingTask = null;
            state = LOBBY;
            Bukkit.getLogger().info("Cancelled travelling task, set state to " + LOBBY);
        }

        if (startingTask != null) {
            startingTask.cancel();
            startingTask = null;
            Bukkit.getLogger().info("Cancelled starting task");
        }

        if (returnToLobbyTask != null) {
            returnToLobbyTask.cancel();
            returnToLobbyTask = null;
            Bukkit.getLogger().info("Cancelled return to lobby task");
        }
    }

    // GAME PROGRESSION METHODS

    public static void attemptTravellingTimer(boolean checkCanChangeState) {
        boolean changeStateSuccess = canChangeState(TRAVELLING);
        if (!changeStateSuccess && checkCanChangeState) return;

        int voteOptions = setupGameVotes();
        if (voteOptions == 0) {
            Util.broadcast(RED + "Failed to start travel: no games to vote for (for this amount of players)");
            return;
        } else {
            Util.broadcast(GREEN + "Vote for one of " + voteOptions + " games!");
        }

        cancelGameTasks();
        travellingTask = new TravellingTask(travellingSeconds).runTaskTimer(Main.plugin, 0, 20);
    }

    public static void travel() {
        if (!canChangeState(STARTING)) return;

        cancelGameTasks();

        game = getHighestVotedGame();
        String worldName = getHighestVotedGameWorld(game.getCode());

        if (WorldManager.setupGameWorld(worldName)) {
            String gameName = game.getCode();

            // register game-specific listener
            // @todo make event handler only work in the game world
            Bukkit.getServer().getPluginManager().registerEvents(game.getEventListener(), Main.plugin);
            Bukkit.getLogger().info("Registered game listener for " + gameName);

            // register game-specific command
            PluginCommand pluginCommand = Main.plugin.getCommand(gameName);
            if (pluginCommand == null) {
                Util.broadcast("Failed to set command executor for /" + gameName);
            } else {
                pluginCommand.setExecutor(game.getCommandExecutor());
                Bukkit.getLogger().info("Set command executor for /" + gameName);
            }

        } else {
            Util.broadcast( RED + "Failed to travel: game world load failed" );
        }
    }

    public static void onGameWorldLoad() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Util.sendToGame(p, false);
        }

        game.travelled(); // @todo custom events & listeners for cleaner code (e.g. GameTravelEvent)
        Util.broadcast(AQUA + "Travelled!");

        startingTask = new StartingTask(startingSeconds).runTaskTimer(Main.plugin, 0, 20);
    }

    public static void start() {
        if (!canChangeState(STARTED)) return;

        cancelGameTasks();

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData pd = PlayerData.get(p);
            pd.gamesPlayed += 1;
            pd.incrementGameType(game.getCode(), "played");
        }

        game.started();
        Util.broadcast(GREEN + "STARTED");
    }

    public static void end(Player pWinner) { // @todo support teams (multiple players too?)
        if (!canChangeState(ENDED)) return;

        if (pWinner == null) {
            Util.broadcast(GOLD + "ENDED: No winners!");
        } else {
            Util.broadcast(GOLD + "ENDED: " + pWinner.getName() + " won!");
        }

        for (Player pOnline : Bukkit.getOnlinePlayers()) {
            PlayerData pd;
            if (pOnline == pWinner) {
                pd = PlayerData.get(pWinner);
                pd.gamesWon += 1;
                pd.incrementGameType(game.getCode(), "won");

                pWinner.setInvulnerable(true);
                pWinner.setGameMode(ADVENTURE);
            } else {
                pd = PlayerData.get(pOnline);
                pd.gamesLost += 1;
                pd.incrementGameType(game.getCode(), "lost");
            }
        }

        game.ended();

        String gameName = game.getCode();

        // unregister game-specific listener
        HandlerList.unregisterAll(game.getEventListener());
        Bukkit.getLogger().info("Unregistered game listener for " + gameName);

        // unregister game-specific command
        PluginCommand pluginCommand = Main.plugin.getCommand(gameName);
        if (pluginCommand == null) {
            Util.broadcast("Failed to remove command executor for /" + gameName);
        } else {
            pluginCommand.setExecutor(null); // @todo test
            Bukkit.getLogger().info("Removed command executor for /" + gameName);
        }

        game = null;

        Util.broadcast(YELLOW + "Returning to lobby in " + returningDelaySeconds);
        returnToLobbyTask = new ReturnToLobbyTask().runTaskLater(Main.plugin, 20 * returningDelaySeconds);
    }

    public static void returnToLobby() {
        if (!canChangeState(LOBBY)) return;

        cancelGameTasks();
        WorldManager.deleteGameWorld();

        attemptTravellingTimer(false);
    }
}
