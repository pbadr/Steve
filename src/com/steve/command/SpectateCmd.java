package com.steve.command;

import com.steve.Util;
import com.steve.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;


public class SpectateCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(RED + "todo"); // @todo console support
            return false;
        }

        Player p = (Player) commandSender;

        if (GameManager.game == null) {
            p.sendMessage(RED + "No game running (game is null)");
            Bukkit.getLogger().severe("Didn't tp player, game is null");
        } else {
            Util.sendToGame(p, true);
        }

        return true;
    }
}
