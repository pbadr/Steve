package com.steve;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class EventListener implements Listener {

    JavaPlugin main;

    public EventListener(Main main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();
        String m = e.getMessage();
        GameMode gm = p.getGameMode();
        e.setCancelled(true);

        int gamesWon = PlayerData.get(uuid).gamesWon;

        String prefix;
        if (gm == GameMode.SPECTATOR) {
            prefix = String.format(ChatColor.DARK_GRAY + "[S]" + ChatColor.UNDERLINE +"[%s]", gamesWon);
        } else {
            prefix = String.format(ChatColor.GRAY + "[%s] ", gamesWon);
        }

        Bukkit.getLogger().info(String.format("%s%s > %s", prefix, n, m));

        for (Player r : e.getRecipients()) {
            r.sendMessage(String.format("%s%s > %s", prefix, n, m));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();
        long currentTime = System.currentTimeMillis();

        if (PlayerData.exists(uuid)) {
            e.setJoinMessage(ChatColor.GREEN + n + " joined");
            PlayerData.get(uuid).lastLoginTimestamp = currentTime;
        } else {
            Const.allPlayerData.add(new PlayerData(n, uuid, currentTime));
            e.setJoinMessage(ChatColor.GREEN + n + " joined for the first time!");
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player pHit = (Player) e.getEntity();
            Player pDamage = (Player) e.getDamager();
            Server server = pHit.getServer();

            String pHitName = pHit.getName();
            String pDamageName = pDamage.getName();

            Bukkit.getLogger().info(String.format("%s was hit by %s", pHitName, pDamageName));
            server.broadcastMessage(String.format("%s got hit by %s", pHitName, pDamageName));

        }
    }

    @EventHandler
    public void onPlayerWalkOnBlock(PlayerMoveEvent e) {
        Location pos = e.getTo();
        Player p = e.getPlayer();

        if(pos == null) return;

        Block b = pos.clone().subtract(0,1,0).getBlock();
        p.sendMessage("Block = " + b.getBlockData().getAsString());
    }

}
