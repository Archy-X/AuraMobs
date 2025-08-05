package dev.aurelium.auramobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.entities.AureliumMob;
import dev.aurelium.auramobs.util.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

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
        plugin.setLanguage(Locale.forLanguageTag(plugin.optionString("language").replace("_", "-")));
        plugin.getScaleManager().loadConfiguration();
        sender.sendMessage(ColorUtils.colorMessage(plugin.getMsg("commands.reload")));
    }

    @Subcommand("summon")
    @CommandPermission("auramobs.summon")
    @Syntax("<type> <level>")
    @CommandCompletion("@entitytypes @level")
    public void onSummon(CommandSender sender, String mobType, int level) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.colorMessage(plugin.getMsg("commands.summon.console")));
            return;
        }

        EntityType type;
        try {
            type = EntityType.valueOf(mobType.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ColorUtils.colorMessage(plugin.getMsg("commands.summon.invalid").replace("{type}", mobType)));
            return;
        }

        if (!type.isSpawnable() || !type.isAlive() || type.getEntityClass() == null || plugin.isInvalidEntity(type.getEntityClass())) {
            sender.sendMessage(ColorUtils.colorMessage(plugin.getMsg("commands.summon.failure")
                    .replace("{mob}", type.name()).replace("{level}", String.valueOf(level))));
            return;
        }

        LivingEntity entity = (LivingEntity) player.getWorld().spawn(player.getLocation(), type.getEntityClass(), ent -> {
            if (ent instanceof LivingEntity living) {
                living.getPersistentDataContainer().set(plugin.getSummonKey(), PersistentDataType.BYTE, (byte) 1);
            }
        });
        new AureliumMob(entity, level, plugin);

        sender.sendMessage(ColorUtils.colorMessage(plugin.getMsg("commands.summon.success")
                .replace("{mob}", type.name()).replace("{level}", String.valueOf(level))));
    }

}
