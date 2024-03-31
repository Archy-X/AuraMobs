package dev.aurelium.auramobs.listeners;

import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.util.MessageUtils;
import dev.aurelium.auraskills.api.event.skill.EntityXpGainEvent;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class EntityXpGainListener implements Listener {

    private final AuraMobs plugin;

    public EntityXpGainListener(AuraMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onXpGain(EntityXpGainEvent event) {
        if (!plugin.optionBoolean("skills_xp.enabled")) return;

        LivingEntity entity = event.getAttacked();

        if (!plugin.isAuraMob(entity)) {
            return;
        }

        Player player = event.getPlayer();
        double sourceXp = event.getAmount();
        int mobLevel = entity.getPersistentDataContainer().getOrDefault(plugin.getMobKey(), PersistentDataType.INTEGER, 0);

        if (mobLevel <= 0) return;

        // Create and evaluate XP formula
        String defaultFormula = MessageUtils.setPlaceholders(player, plugin.optionString("skills_xp.default_formula"))
                .replace("{source_xp}", String.valueOf(sourceXp))
                .replace("{mob_level}", String.valueOf(mobLevel));
        Expression formula = new ExpressionBuilder(defaultFormula).build();
        double modifiedXp = formula.evaluate();
        event.setAmount(modifiedXp);
    }

}
