package com.steve.commands;

import com.steve.Maze;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GenerateMaze implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (player.isOp()) {
                Maze maze = new Maze(20,20, 6, 2, 2, 1);
                maze.generateMaze();
                StringBuilder message = new StringBuilder();
                for (ArrayList<Maze.TILEROLE> i: maze.getMaze())
                {
                    for (Maze.TILEROLE j: i)
                    {
                        switch (j){
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

                Bukkit.getLogger().info("\n" + message.toString());
            }
        }


        return true;
    }
}
