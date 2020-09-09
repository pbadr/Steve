package com.steve;

import com.steve.Commands.GenerateMazeCommand;
import com.steve.Commands.PlatformCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Const.readData();
        new EventListener(this);
        getCommand("spawnplatform").setExecutor(new PlatformCommand());
        getCommand("generatemaze").setExecutor(new GenerateMazeCommand());
        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        Const.writeData();
        Bukkit.getLogger().info("Disabled");
    }
}
