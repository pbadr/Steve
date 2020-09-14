package com.steve.game;

import org.bukkit.event.Listener;

public abstract class BaseGame {
    public abstract int getMinimumPlayers();
    public abstract Listener getEventListener();
    public abstract void travelledTo();
    public abstract void start();
    public abstract void end();
}
