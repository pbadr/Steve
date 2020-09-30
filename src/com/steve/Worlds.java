package com.steve;

import com.steve.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.BEDROCK;

public class Worlds {
    public static final String LOG_PREFIX = "[Worlds] ";
    public static final String WORLDS_PATH = "worlds/";
    // static boolean editorWorldLoaded = false;
    private static final Location lobbyLocation = new Location(Bukkit.getWorld("lobby"), 164, 78, -41);
    public static String currentGameWorld;

    public static Location getLobbyLocation() {
        return lobbyLocation.clone();
    }

    public static boolean setupGameWorld(String worldName) {
        Bukkit.getLogger().info(LOG_PREFIX + "SETTING UP GAME WORLD " + worldName + "...");
        Util.sendTitle(GOLD + "Setting up world (lag)",
                YELLOW + GameManager.game.name() + " @ " + worldName, 10, 1200, 0);
        // duration = 1 minute (est. max load time)

        // check if game world is already loaded
        if (new File("game").exists()) {
            Bukkit.getLogger().info(LOG_PREFIX + "Game world folder exists, deleting");
            deleteGameWorld();
        }

        File worldFile = new File(WORLDS_PATH + worldName);

        Bukkit.getLogger().info(LOG_PREFIX + "Copying from worlds folder");
        try {
            Util.copyFolder(worldFile, new File("game"));
        } catch (Exception e) {
            Bukkit.getLogger().severe(LOG_PREFIX + "Failed to copy folder");
            e.printStackTrace();
            return false;
        }

        Bukkit.getLogger().info(LOG_PREFIX + "Copied, loading world");
        currentGameWorld = worldName;
        // @todo prevent lag, even though world creating can't be put on a separate thread?
        World w = Bukkit.createWorld(new WorldCreator("game"));
        if (w == null) {
            Bukkit.getLogger().info(LOG_PREFIX + "Failed to load game world (var w is still null)");
            currentGameWorld = null;
            return false;
        }

        Util.sendTitle(YELLOW + "Done!", null, 0, 20, 10);
        return true;
    }

    private static boolean deleteWorld(String worldName, boolean saveToWorlds, String saveName) {
        // make sure the world has no players (prevents unloading)
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().getName().equals(worldName)) {
                Util.sendToLobby(p);
                p.sendMessage(GRAY + "Returned to lobby");
            }
            Bukkit.getLogger().info(LOG_PREFIX + "Returned all players in game world to lobby");
        }

        Bukkit.getLogger().info(LOG_PREFIX + "Unloading world " + worldName);
        if (Bukkit.unloadWorld(worldName, saveToWorlds)) {
            Bukkit.getLogger().info(LOG_PREFIX + "Unloaded world " + worldName);
        } else {
            Bukkit.getLogger().severe(LOG_PREFIX + "Failed to unload world " + worldName + ". Was it loaded in the first place?");
        }

        File worldFile = new File(worldName);

        if (saveToWorlds) {
            File destFile = new File(WORLDS_PATH + saveName);

            if (destFile.exists()) {
                Bukkit.getLogger().severe(LOG_PREFIX + "Folder with name " + worldName + " already exists");
                return false;
            }

            Bukkit.getLogger().info(LOG_PREFIX + "Saving to " + WORLDS_PATH);
            try {
                Util.copyFolder(worldFile, destFile);
            } catch (IOException e) {
                Bukkit.getLogger().severe(LOG_PREFIX + "Failed to save world as " + worldName);
                e.printStackTrace();
                return false;
            }
            Bukkit.getLogger().info(LOG_PREFIX + "Saved world as " + worldName);
        }

        Bukkit.getLogger().info(LOG_PREFIX + "Deleting folder " + worldName);

        boolean result;
        try {
            result = Util.deleteFolder(worldFile);
        } catch (Exception e) {
            Bukkit.getLogger().severe(LOG_PREFIX + "Exception caught on deleting world folder " + worldName);
            e.printStackTrace();
            return false;
        }

        if (result) {
            Bukkit.getLogger().info(LOG_PREFIX + "Deleted folder " + worldName);
            return true;
        } else {
            Bukkit.getLogger().severe(LOG_PREFIX + "Failed to delete folder " + worldName);
            return false;
        }
    }

    public static void deleteGameWorld() {
        deleteWorld("game", false, null);
        currentGameWorld = null;
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
                    chunk.setBlock(0, 63, 0, BEDROCK);
                }

                return chunk;
            }
        });

        Bukkit.getLogger().info(LOG_PREFIX + "Creating editor world");
        World w = creator.createWorld();
        if (w == null) {
            Bukkit.getLogger().severe(LOG_PREFIX + "Failed to create editor world");
        } else {
            Bukkit.getLogger().info(LOG_PREFIX + "Created editor world");
        }

        return w;
    }
}
