package com.steve;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.steve.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class Game {
    public static GameState state;

    static int preparingTaskInt;
    static int startingTaskInt;

    public static void attemptPreparingTimer() {
        if (Bukkit.getOnlinePlayers().size() >= 2) {
            state = PREPARING;

            preparingTaskInt = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                int t = 3;

                @Override
                public void run() {
                    if (Bukkit.getOnlinePlayers().size() < 2) {
                        Bukkit.getScheduler().cancelTask(preparingTaskInt);
                        Util.broadcast(RED + "Not enough players!");
                    } else if (t == 0) {
                        Bukkit.getScheduler().cancelTask(preparingTaskInt);
                        starting();
                    } else {
                        Util.broadcast(BLUE + "Preparing... " + t);
                        t -= 1;
                    }
                }

            }, 0, 20);
        }
    }

    public static void starting() {
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
                    start();
                } else {
                    Util.broadcast(AQUA + "Starting... " + t);
                    t -= 1;
                }
            }

        }, 0, 20);
    }

    public static void start() {
        state = RUNNING;
        Util.broadcast(GREEN + "STARTED");
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player p : onlinePlayers) {
            // ..
            p.setGameMode(ADVENTURE);
        }
    }

    public static void end() {

    }
}
