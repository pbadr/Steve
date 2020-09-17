package com.steve.game;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class BaseGame {
    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();
    public abstract String getCommandString();
    public abstract Listener getEventListener();
    public abstract CommandExecutor getCommandExecutor();
    public abstract void travelled();
    public abstract void handleDisconnect(PlayerQuitEvent e);
    public abstract void started();
    public abstract void ended();
}
