package com.steve.game;

import com.steve.Util;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.ChatColor.*;

public class StartingTask extends BukkitRunnable {
    private int t;
    private final String subtitle;

    public StartingTask(int seconds, String subtitle) {
        t = seconds;
        this.subtitle = subtitle;
        Util.broadcast(GREEN + "Starting in " + t + "s");
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

        Util.sendTitle(GREEN + "" + t + "...", subtitle, 0, 20, 0);
        t -= 1;
    }
}
