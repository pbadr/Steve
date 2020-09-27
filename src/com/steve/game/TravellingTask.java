package com.steve.game;

import com.steve.Util;
import com.steve.Voting;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static com.steve.game.GameState.LOBBY;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class TravellingTask extends BukkitRunnable {
    private int t;

    public TravellingTask(int seconds) {
        t = seconds;
        Util.broadcast(AQUA + "Travelling in " + t + "s (" + Bukkit.getOnlinePlayers().size() + " players)");
    }

    @Override
    public void run() {
        if (t == 0) {
            cancel();
            GameManager.travellingTask = null;

            if (Voting.atLeastOneVotableGameSupportsThisAmountOfPlayers()) {
                GameManager.travel();
            } else {
                GameManager.state = LOBBY;
                // @todo start a new call of startTravellingTimer() in an attempt to retry
                Util.broadcast(RED + "Travelling cancelled: too many players left/joined");
            }

            return;
        }

        Util.sendTitle(AQUA +""+ t + "...", AQUA +""+ Bukkit.getOnlinePlayers().size() + " players",
                0, 20, 5);
        t -= 1;
    }
}
