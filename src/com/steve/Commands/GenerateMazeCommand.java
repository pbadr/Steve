package com.steve.Commands;

import com.steve.Maze;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.ArrayList;

public class GenerateMazeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (player.isOp()) {
                Maze maze = new Maze(20,20, 6, 2, 2, 1);
                maze.generateMaze();
                String message = "";
                for (ArrayList<Maze.TILEROLE> i: maze.getMaze())
                {
                    for (Maze.TILEROLE j: i)
                    {
                        switch (j){
                            case EMPTY:
                                message += "O ";
                                break;

                            case ENTRANCE:
                                message += "# ";
                                break;

                            default:
                                break;
                        }
                    }
                    message += "\n\n";
                }

                player.sendMessage(message);
            }
        }


        return true;
    }
}
