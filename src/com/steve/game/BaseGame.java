package com.steve.game;

import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class BaseGame {
    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();
    public abstract String getName(); // the actual, stylized name
    public abstract String getShortName(); // should be /command-able (no spaces etc.)
    public abstract Listener getEventListener(); // shouldn't create a new instance of a Listener!
    public abstract CommandExecutor getCommandExecutor();
    public abstract String[] getSupportedWorlds();
    public abstract Location getSpawnLocation(); // should clone the location, not send it directly
    public abstract void travelled();
    public abstract boolean handleDisconnect(Player p);
    public abstract void started();
    public abstract void ended();
}
