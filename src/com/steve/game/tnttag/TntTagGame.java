package com.steve.game.tnttag;

import com.steve.Main;
import com.steve.PlayerData;
import com.steve.Util;
import com.steve.game.Game;
import com.steve.game.GameManager;
import com.steve.game.GameState;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static com.steve.game.GameState.STARTED;
import static com.steve.game.GameState.STARTING;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.GameMode.SPECTATOR;
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
    public void onTravel() {

    }

    @Override
    public void onDisconnect(Player p, GameState state) {
        List<Player> alivePlayers = GameManager.getAlivePlayers();
        if (state == STARTING) {
            if (alivePlayers.size() <= 1) {
                GameManager.end(null);
            }
        } else if (state == STARTED) {
            if (alivePlayers.size() == 1) {
                GameManager.end(alivePlayers.get(0));
            } else if (alivePlayers.size() == 0) {
                GameManager.end(null);
            } else if (playerFuseTicks.containsKey(p)) {
                playerFuseTicks.remove(p);
                newTntTask();
            }
        }
    }

    @Override
    public void onDeath(Player p) {
        World w = p.getWorld();
        Location pos = p.getLocation();
        PlayerData pd = PlayerData.get(p);

        pd.deaths += 1;
        // w.createExplosion(pos, 4f);
        w.spawnParticle(Particle.CLOUD, pos, 10);
        Util.sendTitle(RED + "Boom!", null, 10, 40, 10);
        Util.broadcast(RED + p.getName() + " exploded!");
        p.setGameMode(SPECTATOR);

        List<Player> alivePlayers = GameManager.getAlivePlayers();
        if (alivePlayers.size() == 1) {
            GameManager.end(alivePlayers.get(0));
        } else if (alivePlayers.size() == 0) {
            GameManager.end(null);
        } else {
            newTntTask();
        }
    }

    @Override
    public void onStart() {
        new UpdateTntFuseTask(this).runTaskTimer(Main.plugin, 0, 1);
        newTntTask();
    }

    @Override
    public void onEnd() {

    }

    @Override
    public Listener getNewEventListener() {
        return new TntTagListener(this);
    }

    @Override
    public CommandExecutor getNewCommandExecutor() {
        return new TntTagCmd(this);
    }

    @Override
    public String[] getSupportedWorlds() {
        return new String[]{"circle1"};
    }

    @Override
    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld("game"), 0, 64, 0);
    }

    public HashMap<Player, Integer> playerFuseTicks = new HashMap<>();
    public int ticksBeforeExplosion = 20;

    public void newTntTask() {
        int index = new Random().nextInt(GameManager.getAlivePlayers().size());
        Object[] players = GameManager.getAlivePlayers().toArray();
        Player p = (Player) players[index];
        playerFuseTicks.put(p, ticksBeforeExplosion);
        Util.broadcast(RED + p.getName() + " is about to explode!");
    }

    public void switchToPlayer(Player pDamager, Player pDamaged) {
        for (Map.Entry<Player, Integer> entry : playerFuseTicks.entrySet()) {
            Player p = entry.getKey();
            int fuseTicks = entry.getValue();

            if (p.equals(pDamager)) { // found pDamager, has an ongoing fuse
                playerFuseTicks.remove(p);
                playerFuseTicks.put(pDamaged, fuseTicks); // copy over current fuse ticks

                Util.broadcast(RED + pDamager.getName() + " is about to explode!");
                pDamager.removePotionEffect(PotionEffectType.SPEED);
                pDamager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 0));
                pDamager.getInventory().setHelmet(new ItemStack(Material.AIR));
                pDamaged.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 3));
                pDamaged.getInventory().setHelmet(new ItemStack(TNT));

                break;
            }
        }
    }
}
