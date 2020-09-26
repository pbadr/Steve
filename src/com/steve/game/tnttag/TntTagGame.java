package com.steve.game.tnttag;

import com.steve.Main;
import com.steve.Util;
import com.steve.game.Game;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Random;

import static org.bukkit.Material.TNT;

public class TntTagGame extends Game {
    @Override // @todo [at]Override needed for Game classes?
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public int getMaxPlayers() {
        return 20;
    }

    @Override
    public Material getVoteMaterial() {
        return TNT;
    }

    @Override
    public String getName() {
        return "TNT Tag";
    }

    @Override
    public String getCode() {
        return "tnttag";
    }

    @Override
    public void travelled() {

    }

    @Override
    public void handleDisconnect(Player p) {
        Util.broadcast(p.getName() + " isn't a tip toe fan");
    }

    @Override
    public void started() {
        int index = new Random().nextInt(Bukkit.getOnlinePlayers().size());
        Object[] players = Bukkit.getOnlinePlayers().toArray();
        Player p = (Player) players[index];
        explodePlayerTask(p);
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
        return new String[]{"tiptoe"};
    }

    @Override
    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld("game"), 0, 65, 0);
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
        p.getInventory().setHelmet(new ItemStack(TNT));
        System.out.println(playerExplodeTasks.keySet().toString());
    }
}
