package com.steve.game;

import com.steve.Util;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class StartingTask extends BukkitRunnable {
    private int t;

    public StartingTask(int seconds) {
        t = seconds;
    }

    @Override
    public void run() {
        if (!GameManager.currentGamePlayerCountOk()) {
            cancel();
            GameManager.startingTask = null;
            Util.broadcast(RED + "Starting cancelled - too many players left or game ended");
            return;
        }

        if (t == 0) {
            GameManager.start();
            return;
        }

        Util.broadcast(GREEN + "Starting in " + t);
        t -= 1;
    }
}
