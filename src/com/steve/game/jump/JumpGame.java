package com.steve.game.jump;

import com.steve.game.Game;
import com.steve.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static org.bukkit.Material.FIRE;

public class JumpGame extends Game {
    @Override
    public String getCode() {
        return "jump";
    }

    @Override
    public CommandExecutor getNewCommandExecutor() {
        return (commandSender, command, s, strings) -> false;
    }

    @Override
    public Listener getNewEventListener() {
        return new Listener() {
        };
    }

    @Override
    public int getMaxPlayers() {
        return 15;
    }

    @Override
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public String getName() {
        return "Jump Showdown";
    }

    @Override
    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld("game"), .5, 65, .5);
    }

    @Override
    public String[] getSupportedWorlds() {
        return new String[] {"tiptoe"};
    }

    @Override
    public Material getVoteMaterial() {
        return FIRE;
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
