package com.steve.game.tiptoe;

import com.steve.Main;
import com.steve.game.BaseGame;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class TipToeGame extends BaseGame {
    public int getMinimumPlayers() {
        return 2;
    }

    public void travelledTo() {

    }

    public void start() {

    }

    public void end() {

    }

    public Listener getEventListener() {
        return new EventListener();
    }

    public static void createFakePlatform(Location pos) {
        int size = 5;

        for (int xOffset = 0; xOffset < size; xOffset++) {
            for (int zOffset = 0; zOffset < size; zOffset++) {
                Block b = pos.getBlock().getRelative(xOffset, 0, zOffset);
                b.setType(Material.GOLD_BLOCK);
                b.setMetadata("isFake", new FixedMetadataValue(Main.plugin, true));
            }
        }
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent e) { // @todo test
            Location pos = e.getTo();
            if (pos == null) return;
            pos = pos.clone();
            Block b = pos.subtract(0,1,0).getBlock();

            if (b.getMetadata("isFake").get(0).asBoolean()) {
                Material blockMaterial = b.getType();
                b.setType(Material.AIR);
                b.removeMetadata("isFake", Main.plugin);
                Location blockPos = b.getLocation().clone(); // @todo clone required or not? see blockPos.add...
                World w = b.getWorld();
                FallingBlock fb = w.spawnFallingBlock(blockPos.add(.5, .5 ,.5),
                        Bukkit.createBlockData(blockMaterial));

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, fb::remove, 20);
            }
            //p.sendMessage("Block = " + b.getBlockData().getAsString());
        }
    }
}
