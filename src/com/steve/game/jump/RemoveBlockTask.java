package com.steve.game.jump;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.STONE;

public class RemoveBlockTask extends BukkitRunnable {
    private final Block block;

    public RemoveBlockTask(Block block) {
        this.block = block;
    }

    @Override
    public void run() {
        block.setType(STONE);
    }
}
