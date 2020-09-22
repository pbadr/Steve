package com.steve.ui;

import com.steve.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FriendsMenu implements Listener {

    private final Inventory inv;

    public FriendsMenu() {

        Bukkit.getPluginManager().registerEvents(this, Main.plugin);

        inv = Bukkit.createInventory(null, 9, "Friends Menu");

        inv.setItem(0, new ItemStack(Material.BEDROCK));

    }

    public void openInventory(Player p) {

        p.openInventory(inv);

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv) return;

        e.setCancelled(true);

    }


}
