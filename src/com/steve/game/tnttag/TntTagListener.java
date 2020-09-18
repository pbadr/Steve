package com.steve.game.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TntTagListener implements Listener {
    final TntTagGame game;

    public TntTagListener(TntTagGame game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerHitByPlayer(EntityDamageByEntityEvent e) {

        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();
            Player pDamager = (Player) e.getDamager();

            if (!game.playerExplodeTasks.containsKey(p) &&
                    game.playerExplodeTasks.containsKey(pDamager)) {
                // player hit by tnt bearer

                Bukkit.getScheduler().cancelTask(game.playerExplodeTasks.get(pDamager));
                game.explodePlayerTask(p);
                game.playerExplodeTasks.remove(pDamager);

                pDamager.removePotionEffect(PotionEffectType.SPEED);
                pDamager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
                pDamager.getInventory().setHelmet(new ItemStack(Material.AIR));
            }
        }
    }
}
