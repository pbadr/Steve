package com.steve.game.tnttag;

import com.steve.Main;
import com.steve.Util;
import com.steve.game.BaseGame;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class TntTagGame extends BaseGame implements Listener, CommandExecutor {
    @Override // @todo [at]Override needed for Game classes?
    public int getMinPlayers() {
        return 2;
    }

    @Override
    public int getMaxPlayers() {
        return 10;
    }

    @Override
    public String getParentCommand() {
        return "tnttag";
    }

    @Override
    public void travelledTo() {

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    public final HashMap<Player, Integer> playerExplodeTasks = new HashMap<>();

    public void explodePlayerTask(Player p) {
        int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
            playerExplodeTasks.remove(p);
            p.damage(20);

            World w = p.getWorld();
            Location pos = p.getLocation();

            w.spawnParticle(Particle.CLOUD, pos, 10);
            end();
            // w.createExplosion(pos, 4f);

        }, 100);
        playerExplodeTasks.put(p, task);

        Util.broadcast(p.getName() + " is about to explode!");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
        p.getInventory().setHelmet(new ItemStack(Material.TNT));
        System.out.println(playerExplodeTasks.keySet().toString());
    }

    @EventHandler
    public void onPlayerHitByPlayer(EntityDamageByEntityEvent e) {

        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();
            Player pDamager = (Player) e.getDamager();

            if (!playerExplodeTasks.containsKey(p) &&
                    playerExplodeTasks.containsKey(pDamager)) {
                // player hit by tnt bearer

                Bukkit.getScheduler().cancelTask(playerExplodeTasks.get(pDamager));
                explodePlayerTask(p);
                playerExplodeTasks.remove(pDamager);

                pDamager.removePotionEffect(PotionEffectType.SPEED);
                pDamager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
                pDamager.getInventory().setHelmet(new ItemStack(Material.AIR));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int index = ThreadLocalRandom.current().nextInt(Bukkit.getOnlinePlayers().size());
        Object[] players = Bukkit.getOnlinePlayers().toArray();
        Player p = (Player) players[index];
        explodePlayerTask(p); // @todo get to work non-static-y

        return true;
    }
}
