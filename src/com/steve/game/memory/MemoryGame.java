package com.steve.game.memory;

import com.steve.game.Game;
import com.steve.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public class MemoryGame extends Game {
    @Override
    public String code() {
        return "memory";
    }

    @Override
    public CommandExecutor newCommandExecutor() {
        return new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                return false;
            }
        };
    }

    @Override
    public Listener newListener() {
        return new Listener() {
        };
    }

    @Override
    public int maxPlayers() {
        return 100;
    }

    @Override
    public int minPlayers() {
        return 1;
    }

    @Override
    public String name() {
        return "Memory";
    }

    @Override
    public Location spawnLocation() {
        return new Location(Bukkit.getWorld("game"), .5, 64, .5);
    }

    @Override
    public String[] worlds() {
        return new String[] {"square1"};
    }

    @Override
    public Material voteMaterial() {
        return YELLOW_WOOL;
    }

    @Override
    public void onDeath(Player p) {

    }

    @Override
    public void onDisconnect(Player p, GameState state) {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTravel() {

    }

    static List<Material> materials = Arrays.asList(RED_WOOL, ORANGE_WOOL, YELLOW_WOOL, LIME_WOOL,
            GREEN_WOOL, LIGHT_BLUE_WOOL, BLUE_WOOL, PURPLE_WOOL);
}
