package com.steve;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EventListener implements Listener {

    JavaPlugin main;

    public EventListener(Main main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String n = p.getName();
        String m = e.getMessage();
        GameMode gm = p.getGameMode();
        e.setCancelled(true);

        long wins = (long) Const.playerData.get(n).get("wins");

        String prefix;
        if (gm == GameMode.SPECTATOR) {
            prefix = String.format(ChatColor.DARK_GRAY + "[S]" + ChatColor.UNDERLINE +"[%s]", wins);
        } else {
            prefix = String.format(ChatColor.GRAY + "[%s] ", wins);
        }

        Bukkit.getLogger().info(String.format("%s%s > %s", prefix, n, m));

        for (Player r : e.getRecipients()) {
            r.sendMessage(String.format("%s%s > %s", prefix, n, m));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String n = p.getName();
        if (!Const.playerData.containsKey(n)) { // new player
            e.setJoinMessage(ChatColor.GREEN + n + " joined for the first time!");
            Const.resetPlayerData(n);
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {

        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player playerHit = (Player) e.getEntity();
            Player playerDamager = (Player) e.getDamager();
            Server server = playerHit.getServer();

            String playerHitName = playerHit.getName();
            String playerDamagerName = playerDamager.getName();

            Bukkit.getLogger().info(String.format("%s was hit by %s", playerHitName, playerDamagerName));
            server.broadcastMessage(String.format("%s got hit by %s", playerHitName, playerDamagerName));

        }
    }

    @EventHandler
    public void onPlayerWalkOnBlock(PlayerMoveEvent e) {
        Location pos = e.getTo();
        Player p = e.getPlayer();

        if(pos == null) return;

        Block b = pos.clone().subtract(0,1,0).getBlock();
        e.getPlayer().sendMessage("Block = " + b.getBlockData().getAsString());
    }

}
