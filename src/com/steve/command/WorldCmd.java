package com.steve.command;

import com.steve.WorldManager;
import com.steve.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;


public class WorldCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(RED + "todo"); // @todo console support
            return false;
        }

        Player p = (Player) commandSender;

        if (args.length == 2 && args[0].equals("tp")) { // @todo test all this
            switch (args[1].toLowerCase()) {
                case "lobby":
                    p.teleport(WorldManager.getLobbyLocation());
                    return true;
                case "editor":
                    World w = Bukkit.getWorld("editor");
                    if (w == null) w = WorldManager.createEditorWorld();
                    p.teleport(new Location(w, 0.5, 65, 0.5));
                    return true;
                case "game":
                    if (GameManager.state == STARTING || GameManager.state == STARTED || GameManager.state == ENDED) {
                        p.teleport(GameManager.game.getSpawnLocation());
                    } else {
                        p.sendMessage(RED + "No game running (so the world isn't loaded)");
                    }
                    return true;
            }

        }

        if (args.length == 2 && args[0].equals("save")) {
            String worldName = args[1];

            if (WorldManager.saveAndDeleteEditorWorld(worldName)) {
                p.sendMessage(GREEN + "Saved to " + worldName + "!");
            } else {
                p.sendMessage(RED + "Failed to save, world name taken?");
            }
            return true;
        }

        return false;
    }
}
