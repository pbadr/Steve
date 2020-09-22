package com.steve;

import com.steve.game.GameManager;
import com.steve.ui.FriendsMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.*;

public class MainListener implements Listener {
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.get(p);
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
        String n = p.getName();
        e.setJoinMessage(GREEN + n + " joined");

        PlayerData.registerIfNotExisting(p);

        PlayerData pd = PlayerData.get(p);
        pd.lastLoginTimestamp = System.currentTimeMillis();
        pd.serverJoins += 1;

        if (GameManager.state == STARTING || GameManager.state == STARTED || GameManager.state == ENDED) {
            Util.sendToGame(p, true);
            p.teleport(GameManager.game.getSpawnLocation());
            p.sendMessage(RED + "Waiting for the next game");
        } else {
            Util.sendToLobby(p);
//            GameManager.attemptTravellingTimer();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String n = p.getName();

        PlayerData.get(p).lastLogoutTimestamp = System.currentTimeMillis();
        e.setQuitMessage(RED + n + " left");
        GameManager.handleDisconnect(p);
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e) { // @todo create a custom event for game listener
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (e.getFinalDamage() < p.getHealth()) return; // check if hit is fatal
        e.setCancelled(true);

        GameManager.handleDeath(p);
    }

    @EventHandler
    public void onPlayerKilledByPlayer(EntityDamageByEntityEvent e) { // @todo create a custom event for game listener
        Bukkit.getLogger().info("oPlKiByPl called"); // @todo test does this also call onPlayerDeath(e) ??
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player pVictim = (Player) e.getEntity();
        if (e.getFinalDamage() < pVictim.getHealth()) return; // check if hit is fatal
        Player pDamager = (Player) e.getEntity();
        e.setCancelled(true);

        pDamager.sendMessage(GREEN + "You killed " + pVictim.getName());
        PlayerData pdDamager = PlayerData.get(pDamager);
        pdDamager.kills += 1;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (!e.getWorld().getName().equals("lobby")) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (!e.getWorld().getName().equals("game")) return;

        GameManager.gameWorldLoaded();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        ItemStack i = e.getItem();

        if(i == null || !i.hasItemMeta())
            return;

        if(i.getItemMeta().getDisplayName().endsWith("Friends")) {
            new FriendsMenu().openInventory(e.getPlayer());
        }


    }

}
