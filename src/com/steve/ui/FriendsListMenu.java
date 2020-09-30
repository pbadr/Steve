package com.steve.ui;

import com.steve.Main;
import com.steve.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FriendsListMenu implements Listener {

    private final Inventory inv;

    public FriendsListMenu(Player player, List<String> friends) {

        Bukkit.getPluginManager().registerEvents(this, Main.plugin);

        inv = Bukkit.createInventory(null, 9, "Friends List");

        inv.setItem(0, new ItemStack(Material.BEDROCK));

        player.openInventory(inv);

    }
//
//    List<String> friends = new ArrayList<>();
//
//            friends.add("Example1");
//            friends.add("Example2");
//
//            for (int i = 0; i < friends.size(); i++) {
//        ItemStack itemStack = new ItemStack(Material.DIRT);
//        ItemMeta itemMeta = itemStack.getItemMeta();
//
//        itemMeta.setDisplayName(ChatColor.RESET + friends.get(i));
//        itemStack.setItemMeta(itemMeta);
//
//        inv.setItem(i, itemStack);
//    }



}
