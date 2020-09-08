package com.steve;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EventListener implements Listener {

    public EventListener(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String n = p.getName();
        String m = e.getMessage();
        e.setCancelled(true);

        if (p.getGameMode() == GameMode.SPECTATOR) {
            Bukkit.getLogger().info(String.format("[S] %s > %s", n, m));

            for (Player r : e.getRecipients()) {
                r.sendMessage(ChatColor.AQUA + String.format("[S] %s > %s", n, m));
            }
        }
    }

}
