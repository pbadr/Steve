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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

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
        ChatColor winsColor = Util.getWinsColor(gamesWon);

        String msg = "";
        if (gm == GameMode.SPECTATOR) {
            msg += "&o[DEAD]";
        }

        msg += String.format("%s[%s] %s &o> &w%s", winsColor, gamesWon, n, m);
        Util.broadcast(msg);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();
        long currentTime = System.currentTimeMillis();

        if (PlayerData.exists(uuid)) {
            PlayerData.get(uuid).lastOnlineTimestamp = currentTime;
            e.setJoinMessage(Util.format("&g" + n + " joined"));
        } else {
            PlayerData.addNew(n, uuid, currentTime);
            e.setJoinMessage(Util.format("&g" + n + " joined for the first time!"));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String n = p.getName();

        PlayerData.get(uuid).lastOnlineTimestamp = System.currentTimeMillis();
        e.setQuitMessage(Util.format("&r" + n + " left"));
    }


    @EventHandler
    public void onPlayerHitByPlayer(EntityDamageByEntityEvent e) {

        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            System.out.println("test1");
            Player p = (Player) e.getEntity();
            Player pDamager = (Player) e.getDamager();

            if (!Util.playerExplodeTask.containsKey(p) &&
                    Util.playerExplodeTask.containsKey(pDamager)) {
                System.out.println("test2");
                // player hit by tnt bearer

                Bukkit.getScheduler().cancelTask(Util.playerExplodeTask.get(pDamager));
                Util.explodePlayerTask(p);
                Util.playerExplodeTask.remove(pDamager);

                pDamager.removePotionEffect(PotionEffectType.SPEED);
                pDamager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
                pDamager.getInventory().setHelmet(new ItemStack(Material.AIR));
            }
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
