package dev.aurelium.aureliummobs.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void sendMessage(String message, Player p) {
        sendChatMessage(message, p);
    }

    public static void sendConsoleMessage(CommandSender sender, String message) {
        sender.sendMessage(ColorUtils.colorMessage(message));
    }

    public static void sendTitleMessage(String message, Player p) {
        p.sendTitle(ColorUtils.colorMessage(message), "", 20, 80, 20);
    }

    public static void sendActionbarMessage(String message, Player p) {
        BaseComponent component = ComponentSerializer.parse(ColorUtils.colorBungee(message))[0];
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public static void sendChatMessage(String message, Player p) {
        p.sendMessage(ColorUtils.colorMessage(message));
    }

    public static String setPlaceholders(Player player, String text) {
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return text;
        } else {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
    }

}
