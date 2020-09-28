package com.steve.game.jump;

import com.steve.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

import static org.bukkit.Material.AIR;

public class BarTask extends BukkitRunnable {
    private int t;

    private final JumpGame game;
    private final World world;
    private final int centreY;
    private final int radius;
    private final long circlePeriod;
    private final int removeBlockTicks;

    public static double speedModifier;

    public BarTask(JumpGame game, World world, int centreY, int radius, double initialSpeedModifier, long circlePeriod,
                   int removeBlockTicks) { // RUNS EVERY TICK
        this.game = game;
        this.world = world;
        this.centreY = centreY;
        this.radius = 10;
        this.circlePeriod = circlePeriod;
        this.removeBlockTicks = removeBlockTicks;
        speedModifier = initialSpeedModifier;
    }

    @Override
    public void run() {
        double x, z;
        for (int currentRadius = 1; currentRadius <= radius; currentRadius++) {
            x = currentRadius * Math.cos(t * speedModifier * 6.283 / circlePeriod);
            z = currentRadius * Math.sin(t * speedModifier * 6.283 / circlePeriod);
            Location pos = new Location(world, x, centreY, z);
            Block b = pos.getBlock();
            b.setType(AIR);

//            for (Map.Entry<Block, BukkitTask> entry : game.removeBlockTasks.entrySet()) {
//                if (entry.getKey().equals(b)) {
//                    entry.getValue().cancel();
//                }
//            }

            game.removeBlockTasks.put(b, new RemoveBlockTask(b).runTaskLater(Main.plugin, removeBlockTicks));
        }

        t++;
    }
}
