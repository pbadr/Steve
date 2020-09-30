package com.steve.ui;

import com.steve.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class FriendsListMenu implements Listener {

    private final Inventory inv;
    private final List<String> friends;

    public FriendsListMenu(Player player, List<String> friends) {

        this.friends = friends;

        Bukkit.getPluginManager().registerEvents(this, Main.plugin);

        inv = Bukkit.createInventory(null, 9, "Friends List");

        for (int i = 0; i < friends.size(); i++) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(friends.get(i)));

            ItemStack itemStack = new ItemStack(Material.DIRT);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(ChatColor.RESET + offlinePlayer.getName());
            itemStack.setItemMeta(itemMeta);

            inv.setItem(i, itemStack);

        }

        player.openInventory(inv);

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv) return;

        e.setCancelled(true);

    }

}
