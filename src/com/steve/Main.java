package com.steve;

import com.steve.Commands.GenerateMazeCommand;
import com.steve.Commands.PlatformCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        PlayerData.readDisk();
        new EventListener(this);

        // >spawnplatform
        getCommand("spawnplatform").setExecutor(new PlatformCommand());

        // >generatemaze
        getCommand("generatemaze").setExecutor(new GenerateMazeCommand());
        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        PlayerData.writeDisk();
        Bukkit.getLogger().info("Disabled");
    }
}
