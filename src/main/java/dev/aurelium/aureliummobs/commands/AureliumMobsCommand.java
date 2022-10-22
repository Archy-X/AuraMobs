package dev.aurelium.aureliummobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.aureliummobs.AureliumMobs;
import dev.aurelium.aureliummobs.util.MessageUtils;
import org.bukkit.command.CommandSender;

public class AureliumMobsCommand extends BaseCommand {

    private final AureliumMobs plugin;

    public AureliumMobsCommand(AureliumMobs plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("aureliummobs.reload")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.onDisable();
        plugin.onEnable();
        MessageUtils.sendConsoleMessage(sender, plugin.getMsg("commands.reload"));
    }


}
