package com.steve.command;

import com.steve.Util;
import com.steve.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;

public class GameCmd implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length >= 1 && args[0].equals("start")) {
            if (args.length >= 2 && args[1].equals("force")) {
                Util.broadcast("to implement"); // @todo implement force start
            } else {
                GameManager.startTravellingTimer();
            }

            return true;
        }

        return false;
    }
}