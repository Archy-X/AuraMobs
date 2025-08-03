package dev.aurelium.auramobs.listeners;

import co.aikar.commands.LogLevel;
import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.util.ColorUtils;
import dev.aurelium.auramobs.util.CustomFunctions;
import dev.aurelium.auramobs.util.MessageUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MobDamage implements Listener {

    private final AuraMobs plugin;

    public MobDamage(AuraMobs plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMobDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!plugin.isAuraMob(entity)) {
            return;
        }

        int level = entity.getPersistentDataContainer().getOrDefault(plugin.getMobKey(), PersistentDataType.INTEGER, 1);
        double resHealth = entity.getHealth() - e.getFinalDamage();
        resHealth = Math.max(resHealth, 0.0);
        String formattedHealth = plugin.getFormatter().format(resHealth);
        try {
            entity.setCustomName(ColorUtils.colorMessage(plugin.optionString("custom_name.format")
                    .replace("{mob}", plugin.getMsg("mobs." + entity.getType().name().toLowerCase(Locale.ROOT)))
                    .replace("{lvl}", Integer.toString(level))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", plugin.getFormatter().format(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
            ));
        } catch (NullPointerException ex){
            entity.setCustomName(ColorUtils.colorMessage(plugin.optionString("custom_name.format")
                    .replace("{mob}", entity.getType().name())
                    .replace("{lvl}", Integer.toString(level))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", plugin.getFormatter().format(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
            ));
        }

    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDamagedByCreeperExplosion(EntityDamageByEntityEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return;

        LivingEntity damager = (LivingEntity) e.getDamager();
        if (!(damager instanceof Creeper)) return;
        if (!plugin.isAuraMob(damager)) return;

        double multiplier = plugin.optionDouble("mob_defaults.damage.explosion-multiplier");
        e.setDamage(scaleNonBossMobDamage(e.getFinalDamage() * multiplier, damager));
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Projectile p)) {
            return;
        }

        if (!(p.getShooter() instanceof LivingEntity entity)) {
            return;
        }

        if (!plugin.isAuraMob(entity)) {
            return;
        }

        double multiplier = plugin.optionDouble("mob_defaults.damage.projectile-multiplier");
        e.setDamage(scaleNonBossMobDamage(e.getFinalDamage() * multiplier, entity));
    }

    private double scaleNonBossMobDamage(double baseDamage, LivingEntity source) {
        double distance = source.getLocation().distance(source.getWorld().getSpawnLocation());
        String prefix = "mob_defaults.";

        PersistentDataContainer data = source.getPersistentDataContainer();
        int level = data.has(plugin.getLevelKey(), PersistentDataType.INTEGER) ?
                data.get(plugin.getLevelKey(), PersistentDataType.INTEGER) : 1;

        Map<String, String> placeholders = Map.of(
                "{mob_damage}", String.valueOf(baseDamage),
                "{level}", String.valueOf(level),
                "{distance}", String.valueOf(distance)
        );

        String formula = applyPlaceholders(plugin.optionString(prefix + "damage.formula"), placeholders);
        double scaledDamage = evaluate(formula);

        String maxFormula = plugin.optionString(prefix + "damage.max");
        if (maxFormula != null && !maxFormula.isEmpty()) {
            String maxExpr = applyPlaceholders(maxFormula, placeholders);
            double maxDamage = evaluate(maxExpr);
            scaledDamage = Math.min(maxDamage, scaledDamage);
        }

        return scaledDamage;
    }

    private String applyPlaceholders(String input, Map<String, String> values) {
        String result = input;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return MessageUtils.setPlaceholders(null, result);
    }

    private double evaluate(String expression) {
        ExpressionBuilder builder = new ExpressionBuilder(expression);
        for (Function func : CustomFunctions.getCustomFunctions()) builder.function(func);
        return builder.build().evaluate();
    }

}
