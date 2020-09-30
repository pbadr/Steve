package com.steve.game.jump;

import com.steve.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.AIR;

public class BarTask extends BukkitRunnable {
    private int t;

    private final JumpGame game;
    private final World world;
    private final int centreY;
    private final int radius;
    private final long circlePeriodTicks;
    private final int removeBlockTicks;

    public static double speedFactor;

    public BarTask(JumpGame game, World world, int centreY, int radius, double initialSpeedFactor,
                   long circlePeriodTicks, int removeBlockTicks) { // RUNS EVERY TICK
        this.game = game;
        this.world = world;
        this.centreY = centreY;
        this.radius = radius;
        this.circlePeriodTicks = circlePeriodTicks;
        this.removeBlockTicks = removeBlockTicks;
        speedFactor = initialSpeedFactor;
    }

    @Override
    public void run() {
        double x, z;
        for (int currentRadius = 1; currentRadius <= radius; currentRadius++) {
            x = currentRadius * Math.cos(t * speedFactor * 6.283 / circlePeriodTicks);
            z = currentRadius * Math.sin(t * speedFactor * 6.283 / circlePeriodTicks);
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
