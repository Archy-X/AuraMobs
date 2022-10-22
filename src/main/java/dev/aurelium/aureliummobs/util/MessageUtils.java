package dev.aurelium.aureliummobs.util;

import dev.aurelium.aureliummobs.AureliumMobs;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void sendMessage(String message, Player p) {
        if (message.startsWith("chat!")) {
            sendChatMessage(message.replace("chat! ","").replace("chat!", ""), p);
        }
        else if (message.startsWith("title!")) {
            sendTitleMessage(message.replace("title! ","").replace("title!", ""), p);
        }
        else if (message.startsWith("actionbar!")) {
            sendActionbarMessage(message.replace("actionbar! ","").replace("actionbar!", ""), p);
        }
    }

    public static void sendConsoleMessage(CommandSender sender, String message) {
        if (message.startsWith("chat!")) {
            sender.sendMessage(ColorUtils.colorMessage(message.replace("chat! ","").replace("chat!", "")));
        }
        else if (message.startsWith("title!")) {
            sender.sendMessage(ColorUtils.colorMessage(message.replace("title! ","").replace("title!", "")));
        }
        else if (message.startsWith("actionbar!")) {
            sender.sendMessage(ColorUtils.colorMessage(message.replace("actionbar! ","").replace("actionbar!", "")));
        }
    }

    public static void sendTitleMessage(String message, Player p) {
        p.sendTitle(ColorUtils.colorMessage(message), "", 20, 80, 20);
    }

    public static void sendActionbarMessage(String message, Player p) {
        BaseComponent component = ComponentSerializer.parse(ColorUtils.colorBungee(message))[0];
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public static void sendChatMessage(String message, Player p){
        p.sendMessage(ColorUtils.colorMessage(message));
    }

    public static String setPlaceholders(Player player, String text){
        if (AureliumMobs.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return text;
        } else {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
    }

}
