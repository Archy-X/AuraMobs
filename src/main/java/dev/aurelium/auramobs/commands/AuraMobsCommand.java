package dev.aurelium.auramobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.util.ColorUtils;
import org.bukkit.command.CommandSender;

import java.util.Locale;

@CommandAlias("auramobs")
public class AuraMobsCommand extends BaseCommand {

    private final AuraMobs plugin;

    public AuraMobsCommand(AuraMobs plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("auramobs.reload")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.getConfigManager().loadConfig();
        plugin.getPolyglot().getMessageManager().loadMessages();
        plugin.setLanguage(new Locale(plugin.optionString("language")));
        plugin.getScaleManager().loadConfiguration();
        sender.sendMessage(ColorUtils.colorMessage(plugin.getMsg("commands.reload")));
    }


}
