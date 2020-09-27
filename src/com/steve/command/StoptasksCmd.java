package com.steve.command;

import com.steve.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static org.bukkit.ChatColor.GREEN;

public class StoptasksCmd implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        GameManager.cancelGameTasks();
        commandSender.sendMessage(GREEN + "Stopped any running tasks");
        return true;
    }
}