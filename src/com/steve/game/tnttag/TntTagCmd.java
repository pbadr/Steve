package com.steve.game.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class TntTagCmd implements CommandExecutor {
    final TntTagGame game;
    public TntTagCmd(TntTagGame game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int index = ThreadLocalRandom.current().nextInt(Bukkit.getOnlinePlayers().size());
        Object[] players = Bukkit.getOnlinePlayers().toArray();
        Player p = (Player) players[index];
        game.explodePlayerTask(p); // @todo get to work non-static-y

        return true;
    }
}
