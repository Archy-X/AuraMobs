package dev.aurelium.auramobs.listeners;

import com.archyx.aureliumskills.api.event.source.EntityXpGainEvent;
import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.util.MessageUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.Monster;
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

        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        if (!plugin.isAuraMob(monster)){
            return;
        }

        Player player = event.getPlayer();
        double sourceXp = event.getAmount();
        int mobLevel = monster.getPersistentDataContainer().get(plugin.getMobKey(), PersistentDataType.INTEGER);

        // Create and evaluate XP formula
        String defaultFormula = MessageUtils.setPlaceholders(player, plugin.optionString("skills_xp.default_formula"))
                .replace("{source_xp}", String.valueOf(sourceXp))
                .replace("{mob_level}", String.valueOf(mobLevel));
        Expression formula = new ExpressionBuilder(defaultFormula).build();
        double modifiedXp = formula.evaluate();
        event.setAmount(modifiedXp);
    }

}
