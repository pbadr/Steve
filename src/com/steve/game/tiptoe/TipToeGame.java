package com.steve.game.tiptoe;

import com.steve.game.Game;
import com.steve.game.GameManager;
import com.steve.game.GameState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static com.steve.game.GameState.STARTED;
import static com.steve.game.GameState.STARTING;
import static org.bukkit.Material.*;

public class TipToeGame extends Game {
    @Override
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public int getMaxPlayers() {
        return 10;
    }

    @Override
    public Material getVoteMaterial() {
        return LEATHER_BOOTS;
    }

    @Override
    public String getName() {
        return "Tip Toe";
    }

    @Override
    public String getCode() {
        return "tiptoe";
    }

    @Override
    public void onTravel() {
        // @todo spawn platforms here (both real and fake)
    }

    @Override
    public void onDisconnect(Player p, GameState state) {
        List<Player> alivePlayers = GameManager.getAlivePlayers();
        if (state == STARTING) {
            if (alivePlayers.size() <= 1) {
                GameManager.end(null);
            }
        } else if (state == STARTED) {
            if (alivePlayers.size() == 1) {
                GameManager.end(alivePlayers.get(0));
            } else if (alivePlayers.size() == 0) {
                GameManager.end(null);
            }
        }
    }

    @Override
    public void onDeath(Player p) {
        p.setVelocity(new Vector());
        p.teleport(getSpawnLocation());
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onEnd() {
    }

    @Override
    public Listener getNewEventListener() {
        return new TipToeListener(this);
    }

    @Override
    public CommandExecutor getNewCommandExecutor() {
        return new TipToeCmd(this);
    }

    @Override
    public String[] getSupportedWorlds() {
        return new String[] { "tiptoe" };
    }

    @Override
    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld("game"), 0, 65, 0);
    }

    final ArrayList<ArrayList<Block>> platformList = new ArrayList<>();
    boolean useSecondaryMaterial = true;

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
