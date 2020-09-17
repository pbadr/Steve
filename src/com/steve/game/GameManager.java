package com.steve.game;

import com.steve.Main;
import com.steve.Util;
import com.steve.game.tiptoe.TipToeGame;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;

public class GameManager {
    public static BaseGame game;
    public static GameState state;

    private static final int travellingSeconds = 1;
    private static final int startingSeconds = 1;

    static int travellingTask;
    static int startingTask;

    public static List<BaseGame> getAllGames() {
        List<BaseGame> allGames = new ArrayList<>();
        // @todo set static class variable with Xgame.class instead of instancing new games every time
        allGames.add(new TipToeGame());

        return allGames;
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

    public static boolean playerCountInvalid() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        return game == null || onlinePlayers < game.getMinPlayers() || game.getMaxPlayers() < onlinePlayers;
    }

    public static void attemptTravellingTimer() {
        if (Bukkit.getScheduler().isCurrentlyRunning(travellingTask)) {
            Util.broadcast(RED + "Failed to travel: task already running");
            return;
        }

        if (!setNewRandomGame()) {
            Util.broadcast(RED + "Failed to travel: no available games (none set or too many/few players)");
            return;
        }

        state = TRAVELLING;

        travellingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            int t = travellingSeconds;

            @Override
            public void run() {

                if (playerCountInvalid()) {
                    Bukkit.getScheduler().cancelTask(travellingTask);
                    Util.broadcast(RED + "Travel cancelled - too many players left/joined");
                    return;
                }

                if (t == 0) {
                    Bukkit.getScheduler().cancelTask(travellingTask);
                    Bukkit.getServer().getPluginManager().registerEvents(game.getEventListener(), Main.plugin);

                    String parentCommand = game.getParentCommand();
                    PluginCommand pluginCommand = Main.plugin.getCommand(parentCommand);
                    if (pluginCommand == null) {
                        Util.broadcast("Failed to set command executor for /" + parentCommand);
                    } else {
                        pluginCommand.setExecutor(game.getCommandExecutor());
                        Bukkit.getLogger().info("Set command executor for /" + parentCommand);
                    }

                    game.travelledTo();
                    startingTimer();
                    return;
                }

                Util.broadcast(BLUE + "Travelling... " + t);
                t -= 1;
            }

        }, 0, 20);
    }

    public static void startingTimer() {
        state = STARTING;

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
                    Util.broadcast(GREEN + "STARTED");
                    state = RUNNING;
                    game.start();
                    return;
                }

                Util.broadcast(AQUA + "Starting... " + t);
                t -= 1;
            }

        }, 0, 20);
    }

    public static void pluginEnabled() {
        state = WAITING;
        attemptTravellingTimer();
    }
}
