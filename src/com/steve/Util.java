package com.steve;

import com.steve.game.GameManager;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static com.steve.game.GameState.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.GameMode.ADVENTURE;
import static org.bukkit.GameMode.SPECTATOR;

public class Util {
    public static final String PLUGIN_PATH = "plugins/Steve.jar";
    public static final String WORLDS_PATH = "worlds/";

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
        p.setGameMode(ADVENTURE);
        p.setAllowFlight(true);
        p.setInvulnerable(true);
        p.setLevel(pd.gamesWon);
        p.setExp(0);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);

        p.teleport(WorldManager.getLobbyLocation());
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

    public static void broadcast(Object msg) {
        Bukkit.broadcastMessage(msg.toString());
    }

    public static void pluginIsBuilt(Timestamp timestamp) {
        broadcast(GREEN + timestamp.toLocalDateTime().toString() + BOLD + " PLUGIN REBUILT - /reload :)");
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
    public static void copyFolder(File source, File target){
        // from: https://bukkit.org/threads/unload-delete-copy-worlds.182814/

        try {
            // @todo what is uid.dat and why ignore?
            List<String> ignore = new ArrayList<>(Arrays.asList(
                    "advancements", "playerdata", "stats", "session.lock", "uid.dat"
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
