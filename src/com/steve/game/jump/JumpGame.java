package com.steve.game.jump;

import com.steve.game.Game;
import com.steve.game.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class JumpGame extends Game {
    @Override
    public String getCode() {
        return null;
    }

    @Override
    public CommandExecutor getNewCommandExecutor() {
        return null;
    }

    @Override
    public Listener getNewEventListener() {
        return null;
    }

    @Override
    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Location getSpawnLocation() {
        return null;
    }

    @Override
    public String[] getSupportedWorlds() {
        return new String[0];
    }

    @Override
    public Material getVoteMaterial() {
        return null;
    }

    @Override
    public void onDeath(Player p) {

    }

    @Override
    public void onDisconnect(Player p, GameState state) {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTravel() {

    }
}
