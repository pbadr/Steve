package com.steve.game.jump;

import com.steve.Main;
import com.steve.PlayerData;
import com.steve.Util;
import com.steve.game.Game;
import com.steve.game.GameManager;
import com.steve.game.GameState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.GameMode.SPECTATOR;
import static org.bukkit.Material.BEDROCK;

public class JumpGame extends Game {
    @Override
    public String code() {
        return "jump";
    }

    @Override
    public CommandExecutor newCommandExecutor() {
        return (commandSender, command, s, args) -> {
            if (args.length == 2 && args[0].equals("speed")) {
                double newSpeed;
                try {
                    newSpeed = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage("Not a valid number!");
                    return false;
                }

                BarTask.speedFactor = newSpeed;
                commandSender.sendMessage("Set bar speed to " + newSpeed);
                return true;
            }
            return false;
        };
    }

    @Override
    public Listener newListener() {
        return new Listener() {

        };
    }

    @Override
    public int maxPlayers() {
        return 15;
    }

    @Override
    public int minPlayers() {
        return 1;
    }

    @Override
    public String name() {
        return "Jump Showdown";
    }

    @Override
    public Location spawnLocation() {
        return new Location(Bukkit.getWorld("game"), .5, 64, .5);
    }

    @Override
    public String[] worlds() {
        return new String[] {"circle1"};
    }

    @Override
    public Material voteMaterial() {
        return BEDROCK;
    }

    @Override
    public void onDeath(Player p) {
        PlayerData pd = PlayerData.get(p);

        pd.deaths += 1;
        Util.sendTitle(RED + "You died!", null, 10, 40, 10);
        Util.broadcast(RED + p.getName() + " died");
        p.setGameMode(SPECTATOR);

        List<Player> alivePlayers = GameManager.getAlivePlayers();
        if (alivePlayers.size() == 1) {
            GameManager.end(alivePlayers.get(0));
        } else if (alivePlayers.size() == 0) {
            GameManager.end(null);
        }
    }

    @Override
    public void onDisconnect(Player p, GameState state) {
        List<Player> alivePlayers = GameManager.getAlivePlayers();
        if (alivePlayers.size() == 1) {
            GameManager.end(alivePlayers.get(0));
        } else if (alivePlayers.size() == 0) {
            GameManager.end(null);
        }
    }

    @Override
    public void onEnd() {
        barTask.cancel();
        for (BukkitTask task : removeBlockTasks.values()) {
            task.cancel();
        }
    }

    @Override
    public void onStart() {
        barTask = new BarTask(this, Bukkit.getWorld("game"), 63, 6, 1,
                20, 4).runTaskTimer(Main.plugin, 0, 1);
    }

    @Override
    public void onTravel() {

    }

    private BukkitTask barTask;
    HashMap<Block, BukkitTask> removeBlockTasks = new HashMap<>();
}
