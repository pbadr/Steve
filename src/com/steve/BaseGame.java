package com.steve;

import org.bukkit.event.Listener;

public interface BaseGame {
    int getMinimumPlayers();
    Listener getEventListener();
    void travelledTo();
    void start();
    void end();
}
