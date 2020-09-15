package com.steve.game;

public abstract class BaseGame {
    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();
    public abstract String getParentCommand();
    public abstract void travelledTo();
    public abstract void start();
    public abstract void end();
}
