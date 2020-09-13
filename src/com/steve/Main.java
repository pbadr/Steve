package com.steve;

import com.steve.commands.*;
import com.steve.commands.social.AddFriend;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JavaPlugin {
    public static Main plugin;
    TimerTask pluginFileWatcherTask;
    static HashMap<String, Object> commandClasses = new HashMap<>();
    static { // add new commands here AND in plugin.yml
        commandClasses.put("tnthelmet", new TntHelmet());
        commandClasses.put("friend", new AddFriend());
        commandClasses.put("generatemaze", new GenerateMaze());
        commandClasses.put("playerdata", new PlayerDataCmd());
        commandClasses.put("spawnplatform", new SpawnPlatform());
        commandClasses.put("test1", new Test1Cmd());
    }

    @Override
    public void onEnable() {
        plugin = this;
        pluginFileWatcherTask = new PluginBuildWatcher();
        new Timer().schedule(pluginFileWatcherTask, new Date(), 1000);

        PlayerData.readDisk();
        GameManager.pluginEnabled();
        new EventListener(plugin);

        commandClasses.forEach((str, executor) -> {
            PluginCommand pluginCommand = getCommand(str);
            if (pluginCommand == null) {
                Bukkit.getLogger().severe("Failed to set command executor for /" + str);
            } else {
                pluginCommand.setExecutor((CommandExecutor) executor);
            }
        });

        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        PlayerData.writeDisk();
        pluginFileWatcherTask.cancel();

        Bukkit.getLogger().info("Disabled");
    }
}
