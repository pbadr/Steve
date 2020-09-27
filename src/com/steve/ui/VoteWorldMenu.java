package com.steve.ui;

import com.steve.Main;
import com.steve.Util;
import com.steve.Voting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Map;

import static org.bukkit.ChatColor.RESET;
import static org.bukkit.Material.FILLED_MAP;

public class VoteWorldMenu implements Listener {
    private final Inventory inv;
    private final String gameCode;

    public VoteWorldMenu(Player p, String gameCode) {
        if (Voting.getGameWorldVotes().get(gameCode).size() != 3) {
            Bukkit.getLogger().severe("Game (" + gameCode + ") world votes size is not equal to 3, instead it's " +
                    Voting.getGameWorldVotes().get(gameCode).size());
        }

        Bukkit.getPluginManager().registerEvents(this, Main.plugin);

        inv = Bukkit.createInventory(null, 9, "Vote for map");
        this.gameCode = gameCode;

        int slot = 3;
        for (Map.Entry<String, Integer> entry : Voting.getGameWorldVotes().get(gameCode).entrySet()) {
            String worldName = entry.getKey();
            ItemStack i = new ItemStack(FILLED_MAP);
            ItemMeta itemMeta = i.getItemMeta();
            if (itemMeta == null) return;
            itemMeta.setDisplayName(RESET + worldName);
            i.setItemMeta(itemMeta);
            inv.setItem(slot, i);
            slot++;
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != inv) return; // not vote game menu
        if (e.getCurrentItem() == null) return; // nothing clicked
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked.getItemMeta() == null) return;
        String clickedWorldName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Voting.incrementGameWorldVote(gameCode, clickedWorldName);
        Player p = (Player) e.getWhoClicked();
        String n = p.getName();

        Util.broadcast(n + " voted for map " + clickedWorldName);

        p.closeInventory();
    }
}
