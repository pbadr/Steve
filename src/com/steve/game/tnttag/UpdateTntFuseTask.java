package com.steve.game.tnttag;

import com.steve.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class UpdateTntFuseTask extends BukkitRunnable {
    TntTagGame game;

    public UpdateTntFuseTask(TntTagGame game) {
        this.game = game;
    }

    @Override
    public void run() { // runs once every tick
        for (Map.Entry<Player, Integer> entry : game.playerFuseTicks.entrySet()) {
            int newFuse = entry.getValue() - 1;
            Player p = entry.getKey();

            if (newFuse == 0) {
                GameManager.handleDeath(p);
                game.playerFuseTicks.remove(p);
                continue;
            }

            game.playerFuseTicks.put(p, newFuse); // update fuse ticks
        }
    }
}
