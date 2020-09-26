package com.steve.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Game {
    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();
    public abstract Material getVoteMaterial();
    public abstract String[] getSupportedWorlds();
    public abstract String getName(); // the actual, stylized name
    public abstract String getCode(); // should be /command-able (no spaces etc.)
    public abstract Listener getEventListener(); // shouldn't create a new instance of a Listener! All EventHandlers should have priority set to >=LOW
    public abstract CommandExecutor getCommandExecutor();
    public abstract Location getSpawnLocation(); // should clone the location, not send it directly @todo world specific spawns
    public abstract void travelled();
    public abstract void handleDisconnect(Player p);
    public abstract void started();
    public abstract void ended();
}
