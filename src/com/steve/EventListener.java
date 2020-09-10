package com.steve;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EventListener implements Listener {
    public EventListener(Main main) {
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
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
        ChatColor winsColor = Util.getWinsColor(ThreadLocalRandom.current().nextInt(0, 101)); // Util.getWinsColor(gamesWon);

        String formatted = "";
        if (gm == GameMode.SPECTATOR) {
            formatted += ChatColor.GRAY + "[DEAD]";
        }

        formatted += String.format("%s[%s] %s %s> %s", winsColor, gamesWon, n, ChatColor.GRAY, ChatColor.WHITE + m);
        Bukkit.broadcastMessage(formatted);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();
        long currentTime = System.currentTimeMillis();

        if (PlayerData.exists(uuid)) {
            PlayerData.get(uuid).lastOnlineTimestamp = currentTime;
            e.setJoinMessage(ChatColor.GREEN + n + " joined");
        } else {
            PlayerData.addNew(n, uuid, currentTime);
            e.setJoinMessage(ChatColor.GREEN + n + " joined for the first time!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();

        PlayerData.get(uuid).lastOnlineTimestamp = System.currentTimeMillis();
        e.setQuitMessage(ChatColor.GREEN + n + " left");
    }


    @EventHandler
    public void onPlayerHitByEntity(EntityDamageByEntityEvent e) {

        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            ItemStack itemTnt = new ItemStack(Material.TNT);

            if(Objects.equals(p.getInventory().getHelmet(), itemTnt)) {
                p.getInventory().setHelmet(new ItemStack(Material.AIR));
            } else {
                p.getInventory().setHelmet(itemTnt);
            }

        }

        if(e.getDamager() instanceof Player) {
            Player pHit = (Player) e.getEntity();
            Player pDamage = (Player) e.getDamager();

            String pHitName = pHit.getName();
            String pDamageName = pDamage.getName();

            Bukkit.broadcastMessage(String.format("%s got hit by %s", pHitName, pDamageName));

        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        // Location pos = e.getTo();
        // Player p = e.getPlayer();

        // if(pos == null) return;

        // Block b = pos.clone().subtract(0,1,0).getBlock();
        //p.sendMessage("Block = " + b.getBlockData().getAsString());
    }

}
