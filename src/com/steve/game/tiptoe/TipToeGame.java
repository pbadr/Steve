package com.steve.game.tiptoe;

import com.steve.Main;
import com.steve.game.BaseGame;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

public class TipToeGame extends BaseGame implements Listener, CommandExecutor {
    @Override
    public int getMinPlayers() {
        return 2;
    }

    @Override
    public int getMaxPlayers() {
        return 10;
    }

    @Override
    public String getParentCommand() {
        return "tiptoe";
    }

    @Override
    public void travelledTo() {

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    public static void createFakePlatform(Location pos) {
        int size = 5;

        for (int xOffset = 0; xOffset < size; xOffset++) {
            for (int zOffset = 0; zOffset < size; zOffset++) {
                Block b = pos.getBlock().getRelative(xOffset, 0, zOffset);
                b.setType(Material.GOLD_BLOCK);
                b.setMetadata("isFake", new FixedMetadataValue(Main.plugin, true));
            }
        }
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent e) { // @todo test
            Location pos = e.getTo();
            if (pos == null) return;
            pos = pos.clone();
            Block b = pos.subtract(0,1,0).getBlock();

            if (b.getMetadata("isFake").get(0).asBoolean()) {
                Material blockMaterial = b.getType();
                b.setType(Material.AIR);
                b.removeMetadata("isFake", Main.plugin);
                Location blockPos = b.getLocation().clone(); // @todo clone required or not? see blockPos.add...
                World w = b.getWorld();
                FallingBlock fb = w.spawnFallingBlock(blockPos.add(.5, .5 ,.5),
                        Bukkit.createBlockData(blockMaterial));

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, fb::remove, 20);
            }
            // p.sendMessage("Block = " + b.getBlockData().getAsString());
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String str, String[] args) {
        if (!(commandSender instanceof Player)) return false;

        if (args.length == 1) {
            switch (args[0]) {
                case "1":
                    TipToeGame.createFakePlatform(((Player) commandSender).getLocation());
                    return true;

                case "2": {
                    Player player = (Player) commandSender;

                    new Platform(4, 4, player.getLocation(), Material.GOLD_BLOCK, false).placeBlocks();
                    return true;

                }
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
                    Bukkit.getLogger().info("\n" + message);
                    return true;
                }
            }
        }

        return false;
    }
}
