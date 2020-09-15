package com.steve.game;

import com.steve.Main;
import com.steve.Util;
import com.steve.game.tiptoe.TipToeGame;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class GameManager {
    public static BaseGame game;
    public static GameState state;

    static int travellingTask;
    static int startingTask;

    public static void travellingTimer() {
        if (Bukkit.getOnlinePlayers().size() >= 2) {
            if (Bukkit.getScheduler().isCurrentlyRunning(travellingTask)) {
                return;
            }

            state = TRAVELLING;
            game = new TipToeGame();

            travellingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                int t = 3;

                @Override
                public void run() {
                    if (Bukkit.getOnlinePlayers().size() < 2) {
                        Bukkit.getScheduler().cancelTask(travellingTask);
                        Util.broadcast(RED + "Not enough players!");
                    } else if (Bukkit.getOnlinePlayers().size() > game.getMaxPlayers()) {
                        Bukkit.getScheduler().cancelTask(travellingTask);
                        Util.broadcast(RED + "Too many players!");
                    } else if (t == 0) {
                        Bukkit.getScheduler().cancelTask(travellingTask);
                        Bukkit.getServer().getPluginManager().registerEvents((Listener) game, Main.plugin);
                        game.travelledTo();
                        startingTimer();
                        PluginCommand pluginCommand = Main.plugin.getCommand(game.getParentCommand());
                        if (pluginCommand == null) {
                            Bukkit.getLogger().severe("Failed to set command executor for /game");
                        } else {
                            pluginCommand.setExecutor((CommandExecutor) game);
                        }
                    } else {
                        Util.broadcast(BLUE + "Preparing... " + t);
                        t -= 1;
                    }
                }

            }, 0, 20);
        }
    }

    public static void startingTimer() {
        state = STARTING;

        startingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            int t = 3;

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() < 2) {
                    Bukkit.getScheduler().cancelTask(startingTask);
                    Util.broadcast(RED + "Not enough players!");
                } else if (t == 0) {
                    Bukkit.getScheduler().cancelTask(startingTask);
                    Util.broadcast(GREEN + "STARTED");
                    state = RUNNING;
                    game.start();
                } else {
                    Util.broadcast(AQUA + "Starting... " + t);
                    t -= 1;
                }
            }

        }, 0, 20);
    }

    public static void pluginEnabled() {
        state = WAITING;
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setGameMode(SURVIVAL);
            // tp all players to lobby, etc
        });
    }
}
