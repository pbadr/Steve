package com.steve.game.tnttag;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TntTagCmd implements CommandExecutor {
    final TntTagGame game;
    public TntTagCmd(TntTagGame game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
