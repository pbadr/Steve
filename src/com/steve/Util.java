package com.steve;

import com.steve.game.GameManager;
import com.steve.ui.VoteGameMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.ADVENTURE;
import static org.bukkit.GameMode.SPECTATOR;

public class Util {
    private static final LinkedHashMap<Integer, ChatColor> winsColors;
    static {
        winsColors = new LinkedHashMap<>();
        winsColors.put(0, DARK_GRAY);
        winsColors.put(5, GRAY);
        winsColors.put(10, WHITE);
        winsColors.put(15, YELLOW);
        winsColors.put(20, GOLD);
        winsColors.put(25, GREEN);
        winsColors.put(30, DARK_GREEN);
        winsColors.put(40, AQUA);
        winsColors.put(50, DARK_AQUA);
        winsColors.put(60, BLUE);
        winsColors.put(70, LIGHT_PURPLE);
        winsColors.put(80, DARK_PURPLE);
        winsColors.put(90, RED);
        winsColors.put(100, DARK_RED);
    }

    public static ChatColor getWinsColor(int gamesWon) {
        ChatColor highestColor = winsColors.get(0);

        for (Integer minWins : winsColors.keySet()) {
            if (minWins == 0) continue;
            if (gamesWon >= minWins) highestColor = winsColors.get(minWins);
        }

        return highestColor;
    }

    public static void sendToLobby(Player p) {
        PlayerData pd = PlayerData.get(p);

        p.getInventory().clear();
        setLobbyItems(p);
        p.setGameMode(ADVENTURE);
        p.setAllowFlight(true);
        p.setInvulnerable(true);
        p.setLevel(pd.gamesWon);
        p.setExp(0);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);

        p.teleport(Worlds.getLobbyLocation());
    }

    public static void sendToGame(Player p, boolean spectator) {
        if (GameManager.state != STARTING && GameManager.state != STARTED && GameManager.state != ENDED) {
            Util.broadcast(RED + "Can't send player " + p.getName() + " to game, state = " + GameManager.state);
            return;
        }

        Location destination = GameManager.game.getSpawnLocation();

        if (spectator) {
            p.setGameMode(SPECTATOR);
        } else {
            p.setGameMode(ADVENTURE);
        }

        p.getInventory().clear();
        p.setAllowFlight(false);
        p.setInvulnerable(true);
        p.setLevel(0);
        p.setExp(0);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);

        p.teleport(destination);
    }

    public static void sendTitle(String big, String small, int fadeIn, int duration, int fadeOut) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(big, small, fadeIn, duration, fadeOut);
        }
    }

    public static void broadcast(Object obj) {
        Bukkit.broadcastMessage(obj.toString());
    }

    public static void pluginIsBuilt(Timestamp timestamp) {
        broadcast(GREEN + timestamp.toLocalDateTime().toString() + BOLD + " PLUGIN REBUILT - /reload :)");
    }

    public static boolean isInZone(Player p, Location loc1, Location loc2) {
        Location pLoc = p.getLocation();
        int pX = pLoc.getBlockX();
        int pY = pLoc.getBlockY();
        int pZ = pLoc.getBlockZ();

        int[] zoneMinMaxX = new int[] {loc1.getBlockX(), loc2.getBlockX()};
        int[] zoneMinMaxY = new int[] {loc1.getBlockY(), loc2.getBlockY()};
        int[] zoneMinMaxZ = new int[] {loc1.getBlockZ(), loc2.getBlockZ()};

        Arrays.sort(zoneMinMaxX);
        Arrays.sort(zoneMinMaxY);
        Arrays.sort(zoneMinMaxZ);

        return zoneMinMaxX[0] <= pX && pX <= zoneMinMaxX[1] &&
                zoneMinMaxY[0] <= pY && pY <= zoneMinMaxY[1] &&
                zoneMinMaxZ[0] <= pZ && pZ <= zoneMinMaxZ[1];
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean deleteFolder(File path) {
        // from: https://bukkit.org/threads/unload-delete-copy-worlds.182814/
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return false;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }

        return path.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyFolder(File source, File target) throws IOException {
        // from: https://bukkit.org/threads/unload-delete-copy-worlds.182814/

        // @todo what is uid.dat and why ignore?
        List<String> ignore = new ArrayList<>(Arrays.asList(
                "advancements", "playerdata", "stats", "session.lock", "uid.dat", "level.dat_old"
        ));

        if (!ignore.contains(source.getName())) {
            if (source.isDirectory()) {
                if (!target.exists()) target.mkdirs();
                String[] files = source.list();
                if (files == null) return;

                for (String file : files) {
                    File srcFile = new File(source, file);
                    File destFile = new File(target, file);
                    copyFolder(srcFile, destFile);
                }
            } else {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
                in.close();
                out.close();
            }
        }
    }

    private static void setLobbyItems(Player p) {
        p.getInventory().setItem(1, VoteGameMenu.getItem());


        // 0 ItemSlot
        ItemStack friendsCompassItem = new ItemStack(Material.COMPASS);
        ItemMeta friendsCompassItemMeta = friendsCompassItem.getItemMeta();

        if (friendsCompassItemMeta == null) return;
        friendsCompassItemMeta.setDisplayName(RESET + "Friends");
        friendsCompassItemMeta.setLore(Collections.singletonList(BLUE + "Test"));

        friendsCompassItem.setItemMeta(friendsCompassItemMeta);

        p.getInventory().setItem(0, friendsCompassItem);
    }

    public static boolean nullCheckItem(ItemStack i) {

        return i != null && i.getItemMeta() != null;

    }
}
