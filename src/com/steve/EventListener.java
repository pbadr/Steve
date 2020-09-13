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

import static com.steve.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class EventListener implements Listener {
    public EventListener(Main plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

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


    @EventHandler
    public void onPlayerHitByPlayer(EntityDamageByEntityEvent e) {

        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();
            Player pDamager = (Player) e.getDamager();

            if (!Util.playerExplodeTasks.containsKey(p) &&
                    Util.playerExplodeTasks.containsKey(pDamager)) {
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
