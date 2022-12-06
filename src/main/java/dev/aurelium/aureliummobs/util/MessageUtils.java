package dev.aurelium.aureliummobs.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static String setPlaceholders(Player player, String text) {
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return text;
        } else {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
    }

}
