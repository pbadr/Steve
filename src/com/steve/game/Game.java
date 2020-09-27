package com.steve.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Game {
    // get methods
    public abstract String getCode(); // should be /command-able (no spaces etc.)
    public abstract CommandExecutor getNewCommandExecutor();
    public abstract Listener getNewEventListener(); // all EventHandlers should have priority set to >=LOW
    public abstract int getMaxPlayers();
    public abstract int getMinPlayers();
    public abstract String getName(); // the actual, stylized name
    public abstract Location getSpawnLocation(); // should clone the location, not send it directly @todo world specific spawns
    public abstract String[] getSupportedWorlds();
    public abstract Material getVoteMaterial();

    // event handlers
    public abstract void onDeath(Player p);
    public abstract void onDisconnect(Player p, GameState state);
    public abstract void onEnd();
    public abstract void onStart();
    public abstract void onTravel();
}
