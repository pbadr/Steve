package com.steve;

import com.steve.game.GameManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class MainListener implements Listener {
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        PlayerData pd = PlayerData.get(uuid);
        String n = p.getName();
        String m = e.getMessage();
        GameMode gm = p.getGameMode();
        e.setCancelled(true);

        int gamesWon = pd.gamesWon;
        ChatColor winsColor = Util.getWinsColor(gamesWon);

        String msg = "";
        if (gm == SPECTATOR) {
            msg += GRAY + "[DEAD]";
        }

        msg += String.format("%s[%s] %s " + GRAY + "> " + WHITE + m, winsColor, gamesWon, n);
        Util.broadcast(msg);
        pd.messagesSent += 1;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();
        long currentTime = System.currentTimeMillis();

        if (!PlayerData.exists(uuid)) {
            PlayerData.register(n, uuid, currentTime);
        }

        PlayerData pd = PlayerData.get(uuid);
        pd.lastOnlineTimestamp = currentTime;
        pd.serverJoins += 1;

        // player properties for in the lobby
        p.setGameMode(ADVENTURE);
        p.setAllowFlight(true);
        p.setInvulnerable(true);
        p.setLevel(pd.gamesWon);
        p.setExp(0);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);

        e.setJoinMessage(GREEN + n + " joined");

        if (GameManager.state  == RUNNING) {
            p.setGameMode(SPECTATOR);
            p.sendMessage(RED + "Waiting for the next game to start");
        } else {
            GameManager.startTravellingTimer();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();

        PlayerData.get(uuid).lastOnlineTimestamp = System.currentTimeMillis();
        e.setQuitMessage(RED + n + " left");
        GameManager.handleDisconnect(p);
    }

}
