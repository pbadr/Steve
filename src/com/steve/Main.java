package com.steve;

import com.steve.Commands.PlatformCommand;
import com.steve.Commands.Steve;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Const.readData();
        new EventListener(this);
        getCommand("steve").setExecutor(new Steve());
        getCommand("spawnplatform").setExecutor(new PlatformCommand());
        Bukkit.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        Const.writeData();
        Bukkit.getLogger().info("Disabled");
    }
}
