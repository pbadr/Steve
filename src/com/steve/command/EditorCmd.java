package com.steve.command;

import com.steve.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;


public class EditorCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(RED + "todo"); // @todo console support
            return false;
        }

        Player p = (Player) commandSender;

        if (args.length >= 1 && args[0].equals("save")) {
            if (args.length == 1) {
                p.sendMessage(RED + "Enter a name as well");
                return false;
            }

            String worldName = args[1];

            if (Worlds.saveAndDeleteEditorWorld(worldName)) {
                p.sendMessage(GREEN + "Saved to " + worldName + "!");
            } else {
                p.sendMessage(RED + "Failed to save, world name taken?");
            }

        } else {
            World w = Bukkit.getWorld("editor");
            if (w == null) w = Worlds.createEditorWorld();
            p.teleport(new Location(w, 0.5, 65, 0.5));
        }

        return true;
    }
}
