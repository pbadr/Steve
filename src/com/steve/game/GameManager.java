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
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class GameManager {
    public static BaseGame game;
    public static GameState state;

    private static final int travellingSeconds = 3;
    private static final int startingSeconds = 10;
    private static final int returningDelaySeconds = 5;

    static int travellingTask;
    static int startingTask;
    static int returnToLobbyTask;

    private static boolean cantChangeState(GameState advanceTo) {
        boolean canChange = false;

        switch (advanceTo) { // inspired by oc.tc PGM source code
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
            state = advanceTo;
        } else {
            Util.broadcast(RED + "Can't change state from " + state + " to " + advanceTo);
        }

        return !canChange;
    }

    private static List<BaseGame> getAllGames() {
        List<BaseGame> allGames = new ArrayList<>();
        // @todo set static class variable with strings as names instead of instancing new games every time
        allGames.add(new TipToeGame());

        return allGames;
    }

    private static boolean setNewRandomGame() {
        // get all games valid for the current amount of players online
        List<BaseGame> availableGames = new ArrayList<>();
        for (BaseGame g : getAllGames()) {
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            if (g.getMinPlayers() <= onlinePlayers && onlinePlayers <= g.getMaxPlayers()) {
                availableGames.add(g);
            }
        }

        // are there available games?
        int size = availableGames.size();
        if (size == 0) {
            return false;
        }

        // pick random game
        int index = ThreadLocalRandom.current().nextInt(size);
        game = availableGames.get(index);
        Util.broadcast("Coming up: " + game.getName());
        return true;
    }

    public static void handleDisconnect(Player p) {
        GameMode gm = p.getGameMode();
        if ((state == STARTING || state == STARTED) && (gm == ADVENTURE || gm == SURVIVAL)) {
            handleDeath(p);
        }
    }

    public static void handleDeath(Player p) {
        List<Player> alivePlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            GameMode gm = onlinePlayer.getGameMode();
            if (gm == ADVENTURE || gm == SURVIVAL) {
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

    public static boolean playerCountInvalid() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        return game == null || onlinePlayers < game.getMinPlayers() || game.getMaxPlayers() < onlinePlayers;
    }

    public static void cancelGameTimerTasks() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        if (scheduler.isCurrentlyRunning(travellingTask)) {
            scheduler.cancelTask(travellingTask);
            Bukkit.getLogger().info("Cancelled travelling task");
        }
        if (scheduler.isCurrentlyRunning(startingTask)) {
            scheduler.cancelTask(startingTask);
            Bukkit.getLogger().info("Cancelled starting task");
        }
        if (scheduler.isCurrentlyRunning(returnToLobbyTask)) {
            scheduler.cancelTask(returnToLobbyTask);
            Bukkit.getLogger().info("Cancelled return to lobby task");
        }
    }

    public static void pluginEnabled() {
        state = LOBBY;
        attemptTravellingTimer();
    }

    // GAME PROCESSION METHODS

    public static void attemptTravellingTimer() {
        if (cantChangeState(TRAVELLING)) return;

        if (!setNewRandomGame()) {
            Util.broadcast(RED + "Failed to start travel: no games available (for this amount of players)");
            return;
        }

        cancelGameTimerTasks();
        travellingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            int t = travellingSeconds;

            @Override
            public void run() {
                if (playerCountInvalid()) {
                    state = LOBBY;
                    Bukkit.getScheduler().cancelTask(travellingTask);
                    Util.broadcast(RED + "Travelling cancelled: too many players left/joined");
                    return; // @todo start a new call of startTravellingTimer() in an attempt to retry
                }

                if (t == 0) {
                    Bukkit.getScheduler().cancelTask(travellingTask);
                    travel();
                    return;
                }

                Util.broadcast(
                        AQUA + "Travelling in " + t + " (" + Bukkit.getOnlinePlayers().size() + " players)"
                );
                t -= 1;
            }

        }, 0, 20);
    }

    public static void travel() {
        if (cantChangeState(STARTING)) return;

        String[] worlds = game.getSupportedWorlds();
        int index = new Random().nextInt(worlds.length);
        if (WorldManager.setupGameWorld(worlds[index])) {
            // register game-specific listener
            // @todo make event handler only work in the game world
            Bukkit.getServer().getPluginManager().registerEvents(game.getEventListener(), Main.plugin);
            Bukkit.getLogger().info("Registered game listener");

            // register game-specific command
            String gameName = game.getShortName();
            PluginCommand pluginCommand = Main.plugin.getCommand(gameName);
            if (pluginCommand == null) {
                Util.broadcast("Failed to set command executor for /" + gameName);
            } else {
                pluginCommand.setExecutor(game.getCommandExecutor());
                Bukkit.getLogger().info("Set command executor for /" + gameName);
            }

        } else {
            Util.broadcast(
                    RED + "Failed to travel: game world load failed"
            );
        }
    }

    public static void onGameWorldLoad() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Util.sendToGame(p, false);
        }

        game.travelled(); // @todo custom events & listeners for cleaner code (e.g. GameTravelEvent)
        Util.broadcast(AQUA + "Travelled!");

        cancelGameTimerTasks();
        startingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            int t = startingSeconds;

            @Override
            public void run() {
                if (playerCountInvalid()) {
                    Bukkit.getScheduler().cancelTask(startingTask);
                    Util.broadcast(RED + "Start cancelled - too many players left/joined");
                    return;
                }

                if (t == 0) {
                    Bukkit.getScheduler().cancelTask(startingTask);
                    start();
                    return;
                }

                Util.broadcast(GREEN + "Starting in " + t);
                t -= 1;
            }

        }, 0, 20);
    }

    public static void start() {
        if (cantChangeState(STARTED)) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData pd = PlayerData.get(p);
            pd.gamesPlayed += 1;
            pd.incrementGameType(game.getShortName(), "played");
        }

        game.started();
        Util.broadcast(GREEN + "STARTED");
    }

    public static void end(Player pWinner) { // @todo support teams (multiple players too?)
        if (cantChangeState(ENDED)) return;

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
                pd.incrementGameType(game.getShortName(), "won");

                pWinner.setInvulnerable(true);
                pWinner.setGameMode(ADVENTURE);
            } else {
                pd = PlayerData.get(pOnline);
                pd.gamesLost += 1;
                pd.incrementGameType(game.getShortName(), "lost");
            }
        }

        game.ended();

        // unregister game-specific listener
        HandlerList.unregisterAll(game.getEventListener());
        Bukkit.getLogger().info("Unregistered game listener");

        // unregister game-specific command
        String gameName = game.getShortName();
        PluginCommand pluginCommand = Main.plugin.getCommand(gameName);
        if (pluginCommand == null) {
            Util.broadcast("Failed to remove command executor for /" + gameName);
        } else {
            pluginCommand.setExecutor(null); // @todo test
            Bukkit.getLogger().info("Removed command executor for /" + gameName);
        }

        game = null;

        Util.broadcast(YELLOW + "Returning to lobby in " + returningDelaySeconds);
        cancelGameTimerTasks();
        returnToLobbyTask = Bukkit.getScheduler().scheduleSyncDelayedTask(
                Main.plugin, GameManager::returnToLobby, 20 * returningDelaySeconds
        );
    }

    public static void returnToLobby() {
        if (cantChangeState(LOBBY)) return;

        WorldManager.deleteGameWorld();

        state = LOBBY;
        attemptTravellingTimer();
    }
}
