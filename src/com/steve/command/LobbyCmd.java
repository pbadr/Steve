package com.steve.command;

import com.steve.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;


public class LobbyCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(RED + "todo"); // @todo console support
            return false;
        }

        Player p = (Player) commandSender;

        Util.sendToLobby(p);
        return true;
    }
}
