package com.steve.game;

import org.bukkit.scheduler.BukkitRunnable;

public class ReturnToLobbyTask extends BukkitRunnable {
    @Override
    public void run() {
        GameManager.returnToLobby();
    }
}
