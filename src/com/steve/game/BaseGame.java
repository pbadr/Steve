package com.steve.game;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public abstract class BaseGame {
    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();
    public abstract String getParentCommand();
    public abstract Listener getEventListener();
    public abstract CommandExecutor getCommandExecutor();
    public abstract void travelledTo();
    public abstract void start();
    public abstract void end();
}
