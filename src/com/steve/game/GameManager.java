package com.steve.game;

import com.steve.Main;
import com.steve.PlayerData;
import com.steve.Util;
import com.steve.game.tiptoe.TipToeGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.ADVENTURE;

public class GameManager {
    public static BaseGame game;
    public static GameState state;

    private static final int travellingSeconds = 2;
    private static final int startingSeconds = 2;
    private static final int endingSeconds = 5;

    static int travellingTask;
    static int startingTask;

    public static boolean canAdvanceToState(GameState advanceTo) {
        switch (advanceTo) { // inspired by oc.tc source code
            case IDLE:
                return false;
            case TRAVELLING:
                return state == IDLE || state == ENDING;
            case STARTING:
                return state == TRAVELLING;
            case RUNNING:
                return state == STARTING;
            case ENDING:
                return state == TRAVELLING || state == STARTING || state == RUNNING;
        }

        return false;
    }

    public static boolean setNewRandomGame() {
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
        return true;
    }

    public static List<BaseGame> getAllGames() {
        List<BaseGame> allGames = new ArrayList<>();
        // @todo set static class variable with Xgame.class instead of instancing new games every time
        allGames.add(new TipToeGame());

        return allGames;
    }

    public static void handleDisconnect(Player p) {
        if (state == RUNNING) {
            game.handleDisconnect(p);
        }
    }

    public static boolean playerCountInvalid() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        return game == null || onlinePlayers < game.getMinPlayers() || game.getMaxPlayers() < onlinePlayers;
    }

    // TRAVEL METHODS

    public static void startTravellingTimer() {
        if (canAdvanceToState(TRAVELLING)) {
            Util.broadcast(
                    RED + "Failed to start travel: can't advance from state " + state + " to " + TRAVELLING
            );
            return;
        }

        if (!setNewRandomGame()) {
            Util.broadcast(RED + "Failed to start travel: no games set or too many/few players");
            return;
        }

        state = TRAVELLING;

        travellingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            int t = travellingSeconds;

            @Override
            public void run() {
                if (playerCountInvalid()) {
                    state = IDLE;
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
                        BLUE + "Travelling in  " + t + " (" + Bukkit.getOnlinePlayers().size() + " players)"
                );
                t -= 1;
            }

        }, 0, 20);
    }

    public static void travel() {
        if (canAdvanceToState(STARTING)) {
            Util.broadcast(
                    RED + "Failed to travel: can't advance from state " + state + " to " + STARTING
            );
            return;
        }

        // register game-specific listener
        Bukkit.getServer().getPluginManager().registerEvents(game.getEventListener(), Main.plugin);

        // register game-specific command
        String gameName = game.getName();
        PluginCommand pluginCommand = Main.plugin.getCommand(gameName);
        if (pluginCommand == null) {
            Util.broadcast("Failed to set command executor for /" + gameName);
        } else {
            pluginCommand.setExecutor(game.getCommandExecutor());
            Bukkit.getLogger().info("Set command executor for /" + gameName);
        }

        // @todo also set to custom world
        Location destination = game.getSpawnLocation();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(ADVENTURE); // @todo probably unnecessary?
            p.setAllowFlight(false);
            p.setInvulnerable(true);
            p.setLevel(0);
            p.setExp(0); // @todo probably unnecessary?
            p.teleport(destination);
        }

        state = STARTING;
        game.travelled();
        Util.broadcast("Travelled!");

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

                Util.broadcast(AQUA + "Starting in " + t);
                t -= 1;
            }

        }, 0, 20);
    }

    public static void start() {
        if (canAdvanceToState(RUNNING)) {
            Util.broadcast(
                    RED + "Failed to start: can't advance from state " + state + " to " + RUNNING
            );
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            PlayerData pd = PlayerData.get(uuid);
            pd.gamesPlayed += 1;
            pd.incrementGameType(game.getName(), "played");
        }

        state = RUNNING;
        game.started();
        Util.broadcast(GREEN + "STARTED");
    }

    public static void end() {
        if (canAdvanceToState(ENDING)) {
            Util.broadcast(
                    RED + "Failed to end: can't advance from state " + state + " to " + ENDING
            );
            return;
        }

        state = ENDING;
        game.ended();
        Util.broadcast(YELLOW + "ENDED, travelling again in " + endingSeconds);
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                Main.plugin, GameManager::startTravellingTimer, 20 * endingSeconds
        );
    }

    public static void pluginEnabled() {
        state = IDLE;
        startTravellingTimer();
    }
}
