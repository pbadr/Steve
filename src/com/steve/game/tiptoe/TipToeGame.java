package com.steve.game.tiptoe;

import com.steve.game.BaseGame;
import com.steve.game.GameManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

import static org.bukkit.GameMode.ADVENTURE;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.YELLOW_WOOL;

public class TipToeGame extends BaseGame {
    ArrayList<ArrayList<Block>> platformList = new ArrayList<>();
    boolean useSecondaryMaterial = true;

    @Override
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public int getMaxPlayers() {
        return 10;
    }

    @Override
    public String getCommandString() {
        return "tiptoe";
    }

    @Override
    public void travelled() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("Get ready kid", null, 5, 10, 5);
        }
        // @todo spawn platforms here (both real and fake)
    }

    @Override
    public void handleDisconnect(PlayerQuitEvent e) {

    }

    @Override
    public void started() {

    }

    @Override
    public void ended() {
    }

    @Override
    public Listener getEventListener() {
        return new TipToeListener(this);
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new TipToeCmd(this);
    }

    @Override
    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld("world"), 0, 65, 0);
    }

    public void createFakePlatform(Location pos) {
        int size = 5;

        ArrayList<Block> list = new ArrayList<>();

        for (int xOffset = 0; xOffset < size; xOffset++) {
            for (int zOffset = 0; zOffset < size; zOffset++) {
                Block b = pos.getBlock().getRelative(xOffset, 0, zOffset);

                if (useSecondaryMaterial) {
                    b.setType(YELLOW_WOOL);
                } else {
                    b.setType(GOLD_BLOCK);
                }

                list.add(b);
            }
        }

        World w = pos.getWorld();
        if (w != null) { // should always result in true
            w.spawnParticle(Particle.EXPLOSION_NORMAL,
                    pos.getX() + (size / 2.0), pos.getY(), pos.getZ() + (size / 2.0), 5);
        }

        useSecondaryMaterial = !useSecondaryMaterial;
        platformList.add(list);
    }
}
