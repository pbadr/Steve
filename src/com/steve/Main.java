package com.steve;

import com.steve.commands.GenerateMazeCommand;
import com.steve.commands.PlatformCommand;
import com.steve.commands.PlayerDataCmd;
import com.steve.commands.social.AddFriend;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JavaPlugin {

    static Main main;
    TimerTask pluginFileWatcherTask;

    @Override
    public void onEnable() {
        main = this;
        PlayerData.readDisk();
        pluginFileWatcherTask = new PluginBuildWatcher();
        new Timer().schedule(pluginFileWatcherTask, new Date(), 1000);

        new EventListener(main);

        getCommand("friend").setExecutor((new AddFriend()));
        getCommand("generatemaze").setExecutor(new GenerateMazeCommand());
        getCommand("playerdata").setExecutor(new PlayerDataCmd());
        getCommand("spawnplatform").setExecutor(new PlatformCommand());

        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        PlayerData.writeDisk();
        pluginFileWatcherTask.cancel();

        Bukkit.getLogger().info("Disabled");
    }
}
