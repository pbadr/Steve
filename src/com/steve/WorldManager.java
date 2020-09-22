package com.steve;

import com.steve.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.Random;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.BEDROCK;

public class WorldManager {
    static boolean editorWorldLoaded = false;
    private static final Location lobbyLocation = new Location(Bukkit.getWorld("lobby"), 164, 78, -41);

    public static Location getLobbyLocation() {
        return lobbyLocation.clone();
    }

    public static boolean setupGameWorld(String worldName) {
        Bukkit.getLogger().info("Setting up world " + worldName);
        Util.sendTitle(GOLD + "Setting up world (lag)", YELLOW + "Playing " +
                GameManager.game.getName() + " on " + worldName + "!", 10, 1200, 0);
        // duration = 1 minute (est. max load time)

        // check if game world is already loaded
        if (new File("game").exists()) {
            Bukkit.getLogger().info("Game world folder apparently already exists, deleting");
            deleteGameWorld();
        }

        File worldFile = new File(Util.WORLDS_PATH + worldName);

        Bukkit.getLogger().info("Copying from worlds folder...");
        Util.copyFolder(worldFile, new File("game")); // @todo check if exception occurred
        Bukkit.getLogger().info("Copied world!");

        Bukkit.getLogger().info("Loading world...");
        // @todo prevent lag, even though world creating can't be put on a separate thread?
        World w = Bukkit.createWorld(new WorldCreator("game"));
        if (w == null) {
            Bukkit.getLogger().info("Failed to load world (is still null)");
            return false;
        }
        Bukkit.getLogger().info("World loaded!");

        Util.sendTitle(YELLOW + "Done!", null, 0, 20, 10);
        return true;
    }

    private static boolean deleteWorld(String worldName, boolean saveToWorlds, String saveName) {
        // make sure the world has no players (prevents unloading)
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().getName().equals(worldName)) {
                Util.sendToLobby(p);
                p.sendMessage(GRAY + "Returned to lobby!");
            }
        }

        Bukkit.getLogger().info("Unloading world...");
        if (Bukkit.unloadWorld(worldName, saveToWorlds)) {
            Bukkit.getLogger().info("Unloaded world");
        } else {
            Bukkit.getLogger().severe("Failed to unload world!");
        }

        File worldFile = new File(worldName);

        if (saveToWorlds) {
            File destFile = new File(Util.WORLDS_PATH + saveName);

            if (destFile.exists()) {
                Bukkit.getLogger().info("World name already exists!");
                return false;
            }

            Bukkit.getLogger().info("Saving to worlds folder...");
            Util.copyFolder(worldFile, destFile);
            Bukkit.getLogger().info("Saved world!");
        }

        Bukkit.getLogger().info("Deleting world folder...");

        boolean result;
        try {
            result = Util.deleteFolder(worldFile);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Exception on deleting world folder!");
            e.printStackTrace();
            return false;
        }

        if (result) {
            Bukkit.getLogger().info("Deleted world folder");
            return true;
        } else {
            Bukkit.getLogger().severe("Failed to delete world folder!");
            return false;
        }
    }

    public static void deleteGameWorld() {
        deleteWorld("game", false, null);
    }

    public static boolean saveAndDeleteEditorWorld(String saveName) {
        return deleteWorld("editor", true, saveName);
    }

    public static World createEditorWorld() {
        // from: https://bukkit.gamepedia.com/Developing_a_World_Generator_Plugin

        WorldCreator creator = new WorldCreator("editor");
        creator.generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
                ChunkData chunk = createChunkData(world);

                if (chunkX == 0 && chunkZ == 0) {
                    chunk.setBlock(0, 64, 0, BEDROCK);
                }

                return chunk;
            }
        });

        Bukkit.getLogger().info("Creating editor world...");
        World w = creator.createWorld();
        if (w == null) {
            Bukkit.getLogger().info("Failed to create editor world!");
        } else {
            Bukkit.getLogger().info("Created editor world!");
        }

        return w;
    }
}
