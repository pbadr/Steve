package com.steve.command;

import com.steve.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TravelCmd implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1 && args[0].equals("force")) {
            commandSender.sendMessage("to implement"); // @todo implement force start
            return false;
        } else {
            GameManager.attemptTravellingTimer(true);
            return true;
        }
    }
}