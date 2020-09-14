package com.steve.game;

import com.steve.Main;
import com.steve.Util;
import org.bukkit.Bukkit;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class GameManager {
    public static BaseGame game;
    public static GameState state;

    static int preparingTaskInt;
    static int startingTaskInt;

    public static void travellingTimer() {
        if (Bukkit.getOnlinePlayers().size() >= 2) {
            state = TRAVELLING;

            preparingTaskInt = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                int t = 3;

                @Override
                public void run() {
                    if (Bukkit.getOnlinePlayers().size() < 2) {
                        Bukkit.getScheduler().cancelTask(preparingTaskInt);
                        Util.broadcast(RED + "Not enough players!");
                    } else if (t == 0) {
                        Bukkit.getScheduler().cancelTask(preparingTaskInt);
                        Bukkit.getServer().getPluginManager().registerEvents(game.getEventListener(), Main.plugin);
                        game.travelledTo();
                        startingTimer();
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

        startingTaskInt = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            int t = 3;

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() < 2) {
                    Bukkit.getScheduler().cancelTask(startingTaskInt);
                    Util.broadcast(RED + "Not enough players!");
                } else if (t == 0) {
                    Bukkit.getScheduler().cancelTask(startingTaskInt);
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
