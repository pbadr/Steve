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
    static HashMap<String, CommandExecutor> commandExecutors = new HashMap<>();
    static { // add new commands here AND in plugin.yml
        commandExecutors.put("friend", new AddFriend());
        commandExecutors.put("playerdata", new PlayerDataCmd());
        commandExecutors.put("game", new GameCmd());
        commandExecutors.put("world", new WorldCmd());
    }

    @Override
    public void onEnable() {
        plugin = this;
        pluginFileWatcherTask = new PluginBuildWatcher();
        new Timer().schedule(pluginFileWatcherTask, new Date(), 1000);

        commandExecutors.forEach((str, executor) -> {
            PluginCommand pluginCommand = getCommand(str);
            if (pluginCommand == null) {
                Bukkit.getLogger().severe("Failed to set command executor for /" + str);
            } else {
                pluginCommand.setExecutor(executor);
                Bukkit.getLogger().info("Set command executor for /" + str);
            }
        });

        PlayerData.readDisk();
        Bukkit.getServer().getPluginManager().registerEvents(new MainListener(), plugin);
        Bukkit.getLogger().info("Enabled");
        // GameManager.pluginEnabled();

        // for game state debugging
        // Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> Util.broadcast("State: " + GameManager.state), 0, 20);
    }

    @Override
    public void onDisable() { // @todo cancel running BukkitTasks?
        PlayerData.writeDisk();
        pluginFileWatcherTask.cancel();

        Bukkit.getLogger().info("Disabled");
    }
}
