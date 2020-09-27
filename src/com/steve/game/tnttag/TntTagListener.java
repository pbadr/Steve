package com.steve.game.tnttag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TntTagListener implements Listener {
    final TntTagGame game;

    public TntTagListener(TntTagGame game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerHitByPlayer(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player & e.getDamager() instanceof Player)) return;

        e.setDamage(0);
        Player pDamaged = (Player) e.getEntity();
        Player pDamager = (Player) e.getDamager();

        game.switchToPlayer(pDamager, pDamaged);
    }
}
