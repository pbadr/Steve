package com.steve.game;

import com.steve.Util;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static com.steve.game.GameState.LOBBY;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class TravellingTask extends BukkitRunnable {
    private int t;

    public TravellingTask(int seconds) {
        t = seconds;
    }

    @Override
    public void run() {
        if (t == 0) {
            cancel();
            GameManager.travellingTask = null;

            if (GameManager.atLeastOneGameSupportsThisAmountOfPlayers()) {
                GameManager.travel();
            } else {
                GameManager.state = LOBBY;
                // @todo start a new call of startTravellingTimer() in an attempt to retry
                Util.broadcast(RED + "Travelling cancelled: too many players left/joined");
            }

            return;
        }

        Util.broadcast(AQUA + "Travelling in " + t + " (" + Bukkit.getOnlinePlayers().size() + " players)");
        t -= 1;
    }
}
