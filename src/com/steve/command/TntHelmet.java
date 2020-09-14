package com.steve.command;

import com.steve.game.GameManager;
import com.steve.game.TntTagGame;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class TntHelmet implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int index = ThreadLocalRandom.current().nextInt(Bukkit.getOnlinePlayers().size());
        Object[] players = Bukkit.getOnlinePlayers().toArray();
        Player p = (Player) players[index];
        GameManager.game.explodePlayerTask(p); // @todo get to work non-static-y

        return true;
    }
}