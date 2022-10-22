package dev.aurelium.aureliummobs.commands;

import dev.aurelium.aureliummobs.util.MessageUtils;
import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AureliumMobsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("aureliummobs")) {
            if (args.length == 0){
                //Send info
                return true;
            }
            else if (args.length == 1){
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!(sender instanceof Player)) {
                        AureliumMobs.getInstance().reloadConfig();
                        AureliumMobs.getInstance().onDisable();
                        AureliumMobs.getInstance().onEnable();
                        MessageUtils.sendConsoleMessage(sender, AureliumMobs.getInstance().getConfigString("messages.plugin-reloaded"));
                    }
                    else {
                        Player player = (Player) sender;
                        if (!player.hasPermission("aureliummobs.reload")) {
                            MessageUtils.sendMessage(AureliumMobs.getInstance().getConfigString("messages.no-permission"), player);
                        }
                        else {
                            AureliumMobs.getInstance().reloadConfig();
                            AureliumMobs.getInstance().onDisable();
                            AureliumMobs.getInstance().onEnable();
                            MessageUtils.sendMessage(AureliumMobs.getInstance().getConfigString("messages.plugin-reloaded"), player);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
