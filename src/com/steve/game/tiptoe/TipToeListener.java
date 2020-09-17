package com.steve.game.tiptoe;

import com.steve.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class TipToeListener implements Listener {
    TipToeGame parent;

    public TipToeListener(TipToeGame parent) {
        this.parent = parent;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        //  e.getPlayer().sendMessage("moved");
        Location pos = e.getTo();
        if (pos == null) return;
        pos = pos.clone();
        Block b = pos.subtract(0,1,0).getBlock();

        for (ArrayList<Block> platform : parent.platformList) {
            if (platform.contains(b)) { // if true, remove entire platform
                for (Block fakeBlock : platform) {
                    Material blockMaterial = fakeBlock.getType();
                    fakeBlock.setType(Material.AIR);
                    Location blockPos = fakeBlock.getLocation().clone();
                    World w = fakeBlock.getWorld();
                    FallingBlock fallingBlock = w.spawnFallingBlock(blockPos.add(.5, 0 ,.5),
                            Bukkit.createBlockData(blockMaterial));

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, fallingBlock::remove, 20);
                }
                parent.platformList.remove(platform);
                break;
            }

            // causes memory leak or is too intensive?
            // Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> game.createFakePlatform(platform.get(0).getLocation()), 60);
        }

        // p.sendMessage("Block = " + b.getBlockData().getAsString());
    }
}