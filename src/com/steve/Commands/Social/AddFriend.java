package com.steve.Commands.Social;

import com.steve.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class AddFriend implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(commandSender instanceof Player) {

            Player p = (Player) commandSender;
            String n = p.getName();

            try {
                if(args[0].toLowerCase().equals("add") && args.length > 1) {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    System.out.println(offlinePlayer.getUniqueId());

                    PlayerData.get(p.getUniqueId())
                            .friendsAdded.add(offlinePlayer.getUniqueId().toString());

                    p.sendMessage(String.format("%s added %s as friend!", n, args[1]));
                } else {
                    p.sendMessage("Please enter a valid username!");
                }
            } catch(Exception e) {
                p.sendMessage("Add a friend with /friend add <username>\n");
            }
        }


        return true;
    }
}
