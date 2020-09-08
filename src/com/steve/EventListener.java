package com.steve;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class EventListener implements Listener {

    public EventListener(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String n = p.getName();
        String m = e.getMessage();
        GameMode gm = p.getGameMode();
        e.setCancelled(true);

        String statusPrefix;
        if (gm == GameMode.SPECTATOR) {
            statusPrefix = ChatColor.DARK_GRAY + "[S] ";
        } else {
            statusPrefix = ChatColor.GRAY + "";
        }

        Bukkit.getLogger().info(String.format("%s%s > %s", statusPrefix, n, m));

        for (Player r : e.getRecipients()) {
            r.sendMessage( String.format("%s%s > %s", statusPrefix, n, m));
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e){

        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            Player pHit = (Player) e.getEntity();
            Player pDamager = (Player) e.getDamager();
            String nHit = pHit.getName();
            String nDamager = pDamager.getName();

            Bukkit.getLogger().info(String.format("%s got hit by %s", nHit, nDamager));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(String.format("%s got hit by %s", nHit, nDamager));
            }
        }
    }

    @EventHandler
    public void onPlayerWalkOnBlock(PlayerMoveEvent e){
        Location pos = e.getTo();

        if(pos == null) return;

        Block b = pos.clone().subtract(0,1,0).getBlock();
        e.getPlayer().sendMessage("Block = " + b.getBlockData().getAsString());
    }

}
