package com.steve;

import com.steve.commands.*;
import com.steve.commands.social.AddFriend;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JavaPlugin {
    static Main plugin;
    TimerTask pluginFileWatcherTask;

    @Override
    public void onEnable() {
        plugin = this;
        PlayerData.readDisk();
        pluginFileWatcherTask = new PluginBuildWatcher();
        new Timer().schedule(pluginFileWatcherTask, new Date(), 1000);

        Game.state = GameState.WAITING;
        new EventListener(plugin);

        getCommand("tnthelmet").setExecutor(new TntHelmet());
        getCommand("friend").setExecutor(new AddFriend());
        getCommand("generatemaze").setExecutor(new GenerateMaze());
        getCommand("playerdata").setExecutor(new PlayerDataCmd());
        getCommand("spawnplatform").setExecutor(new SpawnPlatform());
        getCommand("test1").setExecutor(new Test1Cmd());

        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        PlayerData.writeDisk();
        pluginFileWatcherTask.cancel();

        Bukkit.getLogger().info("Disabled");
    }
}
