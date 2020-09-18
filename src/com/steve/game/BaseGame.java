package com.steve.game;

import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class BaseGame {
    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();
    public abstract String getName();
    public abstract Listener getEventListener();
    public abstract CommandExecutor getCommandExecutor();
    public abstract Location getSpawnLocation();
    public abstract void travelled();
    public abstract void handleDisconnect(Player p);
    public abstract void started();
    public abstract void ended();
}
