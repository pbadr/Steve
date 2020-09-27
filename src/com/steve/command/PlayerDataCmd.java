package com.steve.command;

import com.steve.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;

public class PlayerDataCmd implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length >= 3 && args[0].equals("set")) {
            if (args.length == 3 || args.length == 4) {
                Player p;
                PlayerData target;
                String property;
                int value;

                if (args.length == 3) {
                    if (!(commandSender instanceof Player)) return false;
                    p = (Player) commandSender;

                    target = PlayerData.get(p);
                    property = args[1];
                    value = Integer.parseInt(args[2]);
                } else {
                    p = Bukkit.getPlayer(args[1]);
                    if (p == null) return false;

                    target = PlayerData.get(p);
                    property = args[2];
                    value = Integer.parseInt(args[3]);
                }

                String[] modifiableInts = new String[] { // @todo add more than just numeric properties
                        "serverJoins", "kills", "deaths", "gamesWon", "gamesLost", "gamesPlayed"
                };

                for (String modifiableInt : modifiableInts) {
                    if (property.equals(modifiableInt)) {
                        Object newValue = PlayerData.reflectSet(target, property, value);
                        commandSender.sendMessage(String.format(
                                GREEN + "Set %s to %s of %s", property, newValue, target.name
                        ));

                        return true;
                    }
                }
            }
        }

        if (args.length == 1 && args[0].equals("clear")) {
            PlayerData.emptyMemory();
            commandSender.sendMessage("Success");
            return true;
        }

        if (args.length == 1 && args[0].equals("registerall")) {
            PlayerData.registerAllOnlinePlayers();
            commandSender.sendMessage("Success");
            return true;
        }

        return false;
    }
}