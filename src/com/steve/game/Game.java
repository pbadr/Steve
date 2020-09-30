package com.steve.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Game {
    // get methods
    public abstract String code(); // should be /command-able (no spaces etc.)
    public abstract CommandExecutor newCommandExecutor();
    public abstract Listener newListener(); // all EventHandlers should have priority set to >=LOW
    public abstract int maxPlayers();
    public abstract int minPlayers();
    public abstract String name(); // the actual, stylized name
    public abstract Location spawnLocation(); // should clone the location, not send it directly @todo world specific spawns
    public abstract String[] worlds();
    public abstract Material voteMaterial();

    // event handlers
    public abstract void onDeath(Player p);
    public abstract void onDisconnect(Player p, GameState state);
    public abstract void onEnd();
    public abstract void onStart();
    public abstract void onTravel();
}
