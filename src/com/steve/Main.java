package com.steve;

import com.steve.commands.GenerateMazeCommand;
import com.steve.commands.PlatformCommand;
import com.steve.commands.PlayerDataCmd;
import com.steve.commands.social.AddFriend;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        PlayerData.readDisk();
        new EventListener(this);

        getCommand("friend").setExecutor((new AddFriend()));
        getCommand("generatemaze").setExecutor(new GenerateMazeCommand());
        getCommand("playerdata").setExecutor(new PlayerDataCmd());
        getCommand("spawnplatform").setExecutor(new PlatformCommand());

        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        PlayerData.writeDisk();
        Bukkit.getLogger().info("Disabled");
    }
}
