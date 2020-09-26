package com.steve.ui;

import com.steve.Main;
import com.steve.Util;
import com.steve.game.Game;
import com.steve.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.ChatColor.RESET;

public class VoteGameMenu implements Listener {
    Inventory inv;

    public VoteGameMenu(Player p) {
        assert GameManager.getGameVotes().size() == 3;
        Bukkit.getPluginManager().registerEvents(this, Main.plugin);

        inv = Bukkit.createInventory(null, 9, "Vote");

        int slot = 3;
        for (Map.Entry<String, Integer> entry : GameManager.getGameVotes().entrySet()) {
            String gameCode = entry.getKey();
            Game g = GameManager.getGame(gameCode);
            Material m = g.getVoteMaterial();
            ItemStack i = new ItemStack(m);
            ItemMeta itemMeta = i.getItemMeta();
            if (itemMeta == null) return;
            itemMeta.setDisplayName(RESET + gameCode);
            itemMeta.setLore(Collections.singletonList(
                    g.getMinPlayers() + "-" + g.getMaxPlayers() + " players")
            );
            i.setItemMeta(itemMeta);
            inv.setItem(slot, i);
            slot++;
        }

        p.openInventory(inv);
    }

    public static ItemStack getItem() {
        ItemStack i = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = i.getItemMeta();
        if (itemMeta == null) return null;
        itemMeta.setDisplayName(RESET + "Vote");
        i.setItemMeta(itemMeta);

        return i;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != inv) return;
        if (e.getCurrentItem() == null) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked.getItemMeta() == null) return;
        String clickedCode = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Bukkit.getLogger().info(clickedCode);
        GameManager.incrementGameVote(clickedCode);
        Game g = GameManager.getGame(clickedCode);

        Util.broadcast(e.getWhoClicked().getName() + " voted for " + g.getName());

        // print current vote status in chat
        for (Map.Entry<String, Integer> entry : GameManager.getGameVotes().entrySet()) {
            Util.broadcast(entry.getKey() + " has " + entry.getValue() + " votes");
        }
    }
}