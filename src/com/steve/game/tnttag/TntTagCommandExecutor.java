package com.steve.game.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class TntTagCommandExecutor implements CommandExecutor {
    TntTagGame parent;
    public TntTagCommandExecutor(TntTagGame parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int index = ThreadLocalRandom.current().nextInt(Bukkit.getOnlinePlayers().size());
        Object[] players = Bukkit.getOnlinePlayers().toArray();
        Player p = (Player) players[index];
        parent.explodePlayerTask(p); // @todo get to work non-static-y

        return true;
    }
}
