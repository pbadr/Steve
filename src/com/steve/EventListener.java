package com.steve;

import com.steve.game.GameManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class EventListener implements Listener {
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();
        String m = e.getMessage();
        GameMode gm = p.getGameMode();

        int gamesWon = PlayerData.get(uuid).gamesWon;
        ChatColor winsColor = Util.getWinsColor(gamesWon);

        String msg = "";
        if (gm == SPECTATOR) {
            msg += GRAY + "[DEAD]";
        }

        msg += String.format("%s[%s] %s " + GRAY + "> " + WHITE + m, winsColor, gamesWon, n);
        e.setMessage(msg);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();
        long currentTime = System.currentTimeMillis();

        if (PlayerData.exists(uuid)) {
            PlayerData.get(uuid).lastOnlineTimestamp = currentTime;
        } else {
            PlayerData.register(n, uuid, currentTime);
        }

        e.setJoinMessage(GREEN + n + " joined");

        if (GameManager.state == WAITING) {
            GameManager.travellingTimer();
        } else if (GameManager.state  == RUNNING) {
            p.setGameMode(SPECTATOR);
            p.sendMessage(RED + "Waiting for the next game to start");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();

        PlayerData.get(uuid).lastOnlineTimestamp = System.currentTimeMillis();
        e.setQuitMessage(RED + n + " left");

        if (GameManager.state == RUNNING && Bukkit.getOnlinePlayers().size() <= GameManager.game.getMinimumPlayers()) {
            GameManager.game.end();
            GameManager.state = ENDED;
        }
    }

}
