package com.steve;

import com.steve.game.GameManager;
import com.steve.ui.FriendsMenu;
import com.steve.ui.VoteGameMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler(priority = EventPriority.LOWEST)
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
            p.sendMessage(GRAY + "Waiting for the game to end");
        } else {
            Util.sendToLobby(p);
            // GameManager.attemptTravellingTimer(true);
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

        Bukkit.getLogger().info("ev oPlDe called");
        GameManager.handleDeath(p);
    }

    @EventHandler
    public void onPlayerKilledByPlayer(EntityDamageByEntityEvent e) { // @todo create a custom event for game listener
        if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) return;
        Player pVictim = (Player) e.getEntity();
        if (e.getFinalDamage() < pVictim.getHealth()) return; // check if hit is fatal
        Player pDamager = (Player) e.getDamager();
        e.setCancelled(true);

        Bukkit.getLogger().info("ev oPlKiByPl called"); // @todo test does this also call onPlayerDeath(e) ??
        GameManager.handleKill(pDamager, pVictim);
    }

    @EventHandler
    public void onPlayerReachesVoid(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getLocation().getBlockY() < 0) {
            String worldName = p.getWorld().getName();
            if (worldName.equals("game")) {
                if (GameManager.state == STARTED) {
                    GameManager.handleDeath(p);
                } else if (GameManager.state == STARTING || GameManager.state == ENDED) {
                    p.teleport(GameManager.game.getSpawnLocation());
                }
            } else if (worldName.equals("lobby")) {
                p.teleport(Worlds.getLobbyLocation());
            } else {
                p.teleport(new Location(p.getWorld(), .5, 65, .5));
            }
        }
    }

    @EventHandler
    public void onLobbyWeatherChange(WeatherChangeEvent e) {
        if (!e.getWorld().getName().equals("lobby")) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onGameWorldLoad(WorldLoadEvent e) {
        if (!e.getWorld().getName().equals("game")) return;

        Bukkit.getLogger().info(Worlds.LOG_PREFIX + "Game world loaded");
        GameManager.onGameWorldLoad();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("lobby")) return;
        // ^ events in game world should be handled by game listener

        ItemStack i = e.getItem();

        if (i == null || i.getItemMeta() == null)
            return;

        if (i.getItemMeta().getDisplayName().contains("Friends")) {
            new FriendsMenu().openInventory(e.getPlayer());
        }

        if (i.equals(VoteGameMenu.getItem())) {
            new VoteGameMenu(e.getPlayer());
        }
    }

}
