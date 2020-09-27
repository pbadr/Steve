package com.steve.command;

import com.steve.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static com.steve.game.GameState.*;

public class EndCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (GameManager.state == STARTING || GameManager.state == STARTED) {
            GameManager.end(null);
        }
        return true;
    }
}
