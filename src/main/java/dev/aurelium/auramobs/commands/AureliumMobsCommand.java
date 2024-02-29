package dev.aurelium.auramobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.command.CommandSender;

public class AureliumMobsCommand extends BaseCommand {

    private final AuraMobs plugin;

    public AureliumMobsCommand(AuraMobs plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("aureliummobs.reload")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.onDisable();
        plugin.onEnable();
        sender.sendMessage(plugin.getMsg("commands.reload"));
    }


}
