package com.steve.game.jump;

import com.steve.Main;
import com.steve.Util;
import com.steve.game.GameManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.BEDROCK;
import static org.bukkit.Material.FIRE;

public class BarTask extends BukkitRunnable {
    private int t;
    private final JumpGame game;
    private final int centreY;
    private final World world;
    private final int radius;
    private final long circlePeriod;
    private final double speedModifier;

    public BarTask(JumpGame game, Location centre, int radius, double initialSpeedModifier, long circlePeriod) {
        this.game = game;
        this.centreY = centre.getBlockY();
        this.world = centre.getWorld();
        this.radius = radius;
        this.circlePeriod = circlePeriod;
        speedModifier = initialSpeedModifier;
    }

    @Override
    public void run() {
        double x, z;
        for (int currentRadius = 1; currentRadius <= radius; currentRadius++) {
            x = (int) Math.round(currentRadius * Math.cos(t * speedModifier * 6.283 / circlePeriod));
            z = (int) Math.round(currentRadius * Math.sin(t * speedModifier * 6.283 / circlePeriod));
            Location pos = new Location(world, x, centreY, z);
            pos.getBlock().setType(BEDROCK);

            game.removeBlockTasks.add(new RemoveBlockTask(pos.getBlock()).runTaskLater(Main.plugin, 1));
        }

        for (Player p : GameManager.getAlivePlayers()) {
            if (p.getFireTicks() != -20) {
                Util.broadcast(p.getName() + " should be dead " + p.getFireTicks());
                // GameManager.handleDeath(p);
            }
        }

        t++;
    }
}
