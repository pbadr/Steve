package com.steve;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
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
            PlayerData.register(n, uuid, currentTime);
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

            if (!Util.playerExplodeTasks.containsKey(p) &&
                    Util.playerExplodeTasks.containsKey(pDamager)) {
                System.out.println("test2");
                // player hit by tnt bearer

                Bukkit.getScheduler().cancelTask(Util.playerExplodeTasks.get(pDamager));
                Util.explodePlayerTask(p);
                Util.playerExplodeTasks.remove(pDamager);

                pDamager.removePotionEffect(PotionEffectType.SPEED);
                pDamager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
                pDamager.getInventory().setHelmet(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) { // @todo test
        Location pos = e.getTo();
        if (pos == null) return; // avoid null warning
        pos = pos.clone();
        Block b = pos.subtract(0,1,0).getBlock();

        if (b.getMetadata("isFake").get(0).asBoolean()) {
            Material blockMaterial = b.getType();
            b.setType(Material.AIR);
            b.removeMetadata("isFake", Main.plugin);
            Location blockPos = b.getLocation().clone(); // @todo clone required or not? see blockPos.add...
            World w = b.getWorld();
            FallingBlock fb = w.spawnFallingBlock(blockPos.add(.5, .5 ,.5),
                    Bukkit.createBlockData(blockMaterial));

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, fb::remove, 20);
        }
        //p.sendMessage("Block = " + b.getBlockData().getAsString());
    }

}
