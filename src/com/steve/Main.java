package com.steve;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("Enabling...");
    }

    @Override
    public void onDisable() {
        System.out.println("Disabling...");
    }
}
