package com.steve;

import com.steve.command.*;
import com.steve.command.social.AddFriend;
import com.steve.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
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
        commandClasses.put("friend", new AddFriend());
        commandClasses.put("playerdata", new PlayerDataCmd());
    }

    @Override
    public void onEnable() {
        plugin = this;
        pluginFileWatcherTask = new PluginBuildWatcher();
        new Timer().schedule(pluginFileWatcherTask, new Date(), 1000);

        commandClasses.forEach((str, executor) -> {
            PluginCommand pluginCommand = getCommand(str);
            if (pluginCommand == null) {
                Bukkit.getLogger().severe("Failed to set command executor for /" + str);
            } else {
                pluginCommand.setExecutor((CommandExecutor) executor);
            }
        });

        PlayerData.readDisk();
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
        GameManager.pluginEnabled();
        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() { // @todo cancel running BukkitTasks?
        PlayerData.writeDisk();
        pluginFileWatcherTask.cancel();

        Bukkit.getLogger().info("Disabled");
    }
}
