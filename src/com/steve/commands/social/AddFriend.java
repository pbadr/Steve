package com.steve.commands.social;

import com.steve.Const;
import com.steve.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddFriend implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(commandSender instanceof Player) {

            Player p = (Player) commandSender;
            String n = p.getName();

            try {

                // Getting friend list

//                if(args[0].toLowerCase().equals("list")) {
//                    ArrayList<String> friends = new ArrayList<>();
//
//                    new Thread(() -> {
//
//                        for(String friend :  PlayerData.get(p.getUniqueId()).friendsAdded) {
//                            System.out.println(friend);
//                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(friend));
//                            friends.add(offlinePlayer.getName());
//                        }
//
//
//
//
//                    }).start();
//
//
//                    p.sendMessage(String.format("Total friends (%s)\n" + Arrays.toString(friends.toArray()) + "", friends.size()));
//                }


                // Adding a friend

                if(args[0].toLowerCase().equals("add")) {
                    if(args.length > 1) {
                        if(!args[1].equals(n)) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                            System.out.println(offlinePlayer.getUniqueId());

                            PlayerData.get(p.getUniqueId()).friendsAdded.add(offlinePlayer.getUniqueId().toString());

                            p.sendMessage(String.format("%s added %s as friend!", n, args[1]));
                        } else {
                            p.sendMessage("You cannot add yourself as a friend");
                        }
                    } else {
                        if (args[0].toLowerCase().equals("add"))
                            p.sendMessage("Please enter a valid username!");
                    }
                }

                // Removing a friend

                if(args[0].toLowerCase().equals("remove") && args.length > 1) {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    System.out.println(offlinePlayer.getUniqueId());

                    if(PlayerData.get(p.getUniqueId()).friendsAdded.contains(offlinePlayer.getUniqueId().toString())) {
                        PlayerData.get(p.getUniqueId()).friendsAdded.remove(offlinePlayer.getUniqueId().toString());
                        p.sendMessage(String.format("%s removed %s as friend!", n, args[1]));
                    } else {
                        p.sendMessage("Person not added as friend!");
                    }

                } else {
                    if(args[0].toLowerCase().equals("remove"))
                        p.sendMessage("Please enter a valid username!");
                }

            } catch(Exception e) {
                System.out.println(e);
                p.sendMessage("Add a friend with /friend add <username>\n" +
                                "Remove a friend with /friend remove <username>");
            }
        }


        return true;
    }
}
