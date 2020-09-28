package com.steve.game;

import com.steve.*;
import com.steve.game.jump.JumpGame;
import com.steve.game.tiptoe.TipToeGame;
import com.steve.game.tnttag.TntTagGame;
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
    public static GameState state = LOBBY; // @todo create .setState method instead of assigning to soon-to-be private variable GM.state

    protected static BukkitTask travellingTask;
    protected static BukkitTask startingTask;
    protected static BukkitTask returnToLobbyTask;

    private static final int travellingSeconds = 5;
    private static final int startingSeconds = 5;
    private static final int returningDelaySeconds = 2;


    public static List<Game> getAllGames() {
        List<Game> allGames = new ArrayList<>();
        // @todo set static class variable with strings as names instead of instancing new games every time
        allGames.add(new TipToeGame());
        allGames.add(new TntTagGame());
        allGames.add(new JumpGame());

        return allGames;
    }

    public static boolean canChangeState(GameState changeTo) {
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
                canChange = state == STARTING || state == STARTED;
                break;
        }

        if (canChange) {
            state = changeTo;
        } else {
            Util.broadcast(RED + "Can't change state from " + state + " to " + changeTo);
        }

        return canChange;
    }

    public static boolean currentGamePlayerCountOk() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        return game != null && onlinePlayers >= game.getMinPlayers() && onlinePlayers <= game.getMaxPlayers();
    }

    public static List<Player> getAlivePlayers() {
        List<Player> alivePlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            GameMode gm = p.getGameMode();
            if (p.getWorld().getName().equals("game") && (gm == ADVENTURE || gm == SURVIVAL)) {
                alivePlayers.add(p);
            }
        }

        return alivePlayers;
    }

    public static boolean registerGameComponents() {
        if (game == null) {
            Bukkit.getLogger().severe("Can't register game components for null game");
            return false;
        }

        String gameName = game.getCode();

        // register game-specific listener
        // @todo make event handler only work in the game world
        try {
            Bukkit.getPluginManager().registerEvents(game.getNewEventListener(), Main.plugin);
        } catch (NullPointerException e) {
            Bukkit.getLogger().severe("Failed to register game listener for " + gameName);
            e.printStackTrace();
            return false;
        }
        Bukkit.getLogger().info("Registered game listener for " + gameName);

        // register game-specific command
        PluginCommand pluginCommand = Main.plugin.getCommand(gameName);
        if (pluginCommand == null) {
            Bukkit.getLogger().severe("Failed to set command executor for /" + gameName);
            return false;
        }

        pluginCommand.setExecutor(game.getNewCommandExecutor());
        Bukkit.getLogger().info("Set command executor for /" + gameName);
        return true;
    }

    public static boolean unregisterGameComponents() {
        if (game == null) {
            Bukkit.getLogger().severe("Can't unregister game components for null game");
            return false;
        }

        String gameName = game.getCode();

        // unregister game-specific listener
        HandlerList.unregisterAll(game.getNewEventListener());
        Bukkit.getLogger().info("Unregistered game listener for " + gameName);

        // unregister game-specific command
        PluginCommand pluginCommand = Main.plugin.getCommand(gameName);
        if (pluginCommand == null) {
            Util.broadcast("Failed to remove command executor for /" + gameName);
            return false;
        } else {
            pluginCommand.setExecutor(null); // @todo test
            Bukkit.getLogger().info("Removed command executor for /" + gameName);
            return true;
        }
    }

    // EVENT HANDLERS

    public static void handleDisconnect(Player p) {
        if (state != STARTING && state != STARTED) {
            Bukkit.getLogger().info("Ignoring unimportant gamestate-filtered disconnect of " + p.getName());
            return;
        }

        GameMode gm = p.getGameMode();
        if (gm != ADVENTURE && gm != SURVIVAL) {
            Bukkit.getLogger().info("Ignoring unimportant gamemode-filtered disconnect of " + p.getName());
            return;
        }

        game.onDisconnect(p, state);
    }

    public static void handleDeath(Player p) {
        if (state != STARTED) {
            p.sendMessage("Game hasn't started yet, whoops");
            Bukkit.getLogger().info("Ignoring invalid gamestate-filtered death of " + p.getName());
            return;
        }

        GameMode gm = p.getGameMode();
        if (gm != ADVENTURE && gm != SURVIVAL) {
            p.sendMessage("You can't even die, whoops");
            Bukkit.getLogger().info("Ignoring invalid gamemode-filtered death of " + p.getName());
            return;
        }

        game.onDeath(p);
    }

    public static void handleKill(Player pDamager, Player pVictim) {
        pDamager.sendMessage(GREEN + "You killed " + pVictim.getName());
        PlayerData pdDamager = PlayerData.get(pDamager);
        pdDamager.kills += 1;
    }

    // GAME PROGRESSION METHODS

    public static void attemptTravellingTimer(boolean checkCanChangeState) {
        boolean changeStateSuccess = canChangeState(TRAVELLING);
        if (!changeStateSuccess && checkCanChangeState) return;
        cancelGameTasks();

        int voteOptions = Voting.setupGameVotes();
        if (voteOptions == 0) {
            Util.broadcast(RED + "Failed to start travel: no games to vote for (for this amount of players)");
            return;
        } else {
            Util.broadcast(GREEN + "Vote for one of " + voteOptions + " games!");
        }

        travellingTask = new TravellingTask(travellingSeconds).runTaskTimer(Main.plugin, 0, 20);
    }

    public static void travel() {
        if (!canChangeState(STARTING)) return;
        cancelGameTasks();

        game = Voting.getHighestVotedGame();
        assert game != null; // @todo this probably does nothing
        String worldName = Voting.getHighestVotedGameWorld(game.getCode());
        Voting.clearVotes();

        if (!registerGameComponents()) {
            Util.broadcast(RED + "Failed to travel: couldn't register game components");
            return;
        }

        if (!Worlds.setupGameWorld(worldName)) {
            Util.broadcast( RED + "Failed to travel: game world load failed");
        }
    }

    public static void onGameWorldLoad() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Util.sendToGame(p, false);
        }

        game.onTravel(); // @todo custom events & listeners for cleaner code (e.g. GameTravelEvent)

        startingTask = new StartingTask(
                startingSeconds, YELLOW + GameManager.game.getName() + " @ " + Worlds.currentGameWorld).
                runTaskTimer(Main.plugin, 0, 20);
    }

    public static void start() {
        if (!canChangeState(STARTED)) return;
        cancelGameTasks();

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData pd = PlayerData.get(p);
            pd.gamesPlayed += 1;
            pd.incrementGameStat(game.getCode(), "played");
        }

        game.onStart();
        Util.sendTitle(GREEN + "START", null, 0, 20, 0);
        Util.broadcast(GREEN + "START");
    }

    public static void end(Player pWinner) { // @todo support teams (multiple players too?)
        if (!canChangeState(ENDED)) return;
        cancelGameTasks();

        if (pWinner == null) {
            Util.broadcast(GOLD + "Game ended!");
        } else {
            Util.broadcast(GOLD + pWinner.getName() + " won!");
        }

        for (Player pOnline : Bukkit.getOnlinePlayers()) {
            PlayerData pd;
            pOnline.setGameMode(SPECTATOR);

            if (pOnline == pWinner) {
                pd = PlayerData.get(pWinner);
                pd.gamesWon += 1;
                pd.incrementGameStat(game.getCode(), "won");

                pWinner.sendMessage(GOLD + "You won!");
                pWinner.sendTitle(GOLD + "You won!", null, 10, 40, 10);
            } else if (pWinner != null) {
                pd = PlayerData.get(pOnline);
                pd.gamesLost += 1;
                pd.incrementGameStat(game.getCode(), "lost");

                pWinner.sendMessage(RED + "You lost!");
                pWinner.sendTitle(RED + "You lost!", null, 10, 40, 10);
            }
        }

        game.onEnd();

        if (!unregisterGameComponents()) {
            Bukkit.getLogger().severe("Failed to unregister game components");
        }

        Util.broadcast(YELLOW + "Returning to lobby in " + returningDelaySeconds);
        returnToLobbyTask = new ReturnToLobbyTask().runTaskLater(Main.plugin, 20 * returningDelaySeconds);
    }

    public static void returnToLobby() {
        if (!canChangeState(LOBBY)) return;
        cancelGameTasks();

        game = null;
        Worlds.deleteGameWorld();

        attemptTravellingTimer(false);
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
}
