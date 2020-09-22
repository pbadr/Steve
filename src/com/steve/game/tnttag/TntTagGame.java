package com.steve.game.tnttag;

import com.steve.Main;
import com.steve.Util;
import com.steve.game.BaseGame;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class TntTagGame extends BaseGame {
    @Override // @todo [at]Override needed for Game classes?
    public int getMinPlayers() {
        return 2;
    }

    @Override
    public int getMaxPlayers() {
        return 20;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getShortName() {
        return "tnttag";
    }

    @Override
    public void travelled() {

    }

    @Override
    public boolean handleDisconnect(Player p) {
        return false;
    }

    @Override
    public void started() {

    }

    @Override
    public void ended() {

    }

    @Override
    public Listener getEventListener() {
        return new TntTagListener(this);
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new TntTagCmd(this);
    }

    @Override
    public String[] getSupportedWorlds() {
        return new String[0];
    }

    @Override
    public Location getSpawnLocation() {
        return null;
    }

    public final HashMap<Player, Integer> playerExplodeTasks = new HashMap<>();

    public void explodePlayerTask(Player p) {
        int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
            playerExplodeTasks.remove(p);
            p.damage(20);

            World w = p.getWorld();
            Location pos = p.getLocation();

            w.spawnParticle(Particle.CLOUD, pos, 10);
            ended();
            // w.createExplosion(pos, 4f);

        }, 100);
        playerExplodeTasks.put(p, task);

        Util.broadcast(p.getName() + " is about to explode!");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
        p.getInventory().setHelmet(new ItemStack(Material.TNT));
        System.out.println(playerExplodeTasks.keySet().toString());
    }
}
