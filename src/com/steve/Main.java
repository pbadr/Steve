package com.steve;

import com.steve.Commands.GenerateMazeCommand;
import com.steve.Commands.PlatformCommand;
import com.steve.Commands.Social.AddFriend;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        PlayerData.readDisk();
        new EventListener(this);

        // > spawnPlatform
        getCommand("spawnplatform").setExecutor(new PlatformCommand());

        // > generateMaze
        getCommand("generatemaze").setExecutor(new GenerateMazeCommand());


        // > addFriend
        getCommand("friend").setExecutor((new AddFriend()));

        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        PlayerData.writeDisk();
        Bukkit.getLogger().info("Disabled");
    }
}
