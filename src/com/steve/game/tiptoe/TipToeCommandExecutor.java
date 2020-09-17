package com.steve.game.tiptoe;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TipToeCommandExecutor implements CommandExecutor {
    TipToeGame parent;

    public TipToeCommandExecutor(TipToeGame parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String str, String[] args) {
        if (!(commandSender instanceof Player)) return false;

        if (args.length == 1) {
            switch (args[0]) {
                case "1":
                    parent.createFakePlatform(((Player) commandSender).getLocation());
                    return true;

                case "3": {
                    Player player = (Player) commandSender;
                    Maze maze = new Maze(20, 20, 6, 2, 2, 1);
                    maze.generateMaze();

                    StringBuilder message = new StringBuilder();
                    for (ArrayList<Maze.TILEROLE> i : maze.getMaze()) {
                        for (Maze.TILEROLE j : i) {
                            switch (j) {
                                case EMPTY:
                                    message.append("O ");
                                    break;

                                case ENTRANCE:
                                    message.append("# ");
                                    break;

                                case EXIT:
                                    message.append("E ");
                                    break;

                                case WAYPOINT:
                                    message.append("P ");
                                    break;

                                case PATH:
                                    message.append("@ ");
                                    Bukkit.getLogger().info("PAth");
                                    break;

                                default:
                                    break;
                            }
                        }
                        message.append("\n\n");
                    }

                    player.sendMessage("\n" + message);
                    return true;
                }
            }
        }

        return false;
    }
}