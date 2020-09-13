package com.steve.commands;

import com.steve.BaseGame;
import com.steve.Main;
import com.steve.Util;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class TestGame2020 implements BaseGame {
    public final HashMap<Player, Integer> playerExplodeTasks = new HashMap<>();

    public void explodePlayerTask(Player p) {
        int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
            playerExplodeTasks.remove(p);
            p.damage(p.getHealth());

            World w = p.getWorld();
            Location pos = p.getLocation();

            w.spawnParticle(Particle.CLOUD, pos, 10);
            end();
            // w.createExplosion(pos, 4f);

        }, 100);
        playerExplodeTasks.put(p, task);

        Util.steveBroadcast(p.getName() + " is about to explode!");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
        p.getInventory().setHelmet(new ItemStack(Material.TNT));
        System.out.println(playerExplodeTasks.keySet().toString());
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

    @Override
    public int getMinimumPlayers() {
        return 2;
    }

    @Override
    public Listener getEventListener() {
        return new EventListener();
    }

    public class EventListener implements Listener {
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
    }
}
