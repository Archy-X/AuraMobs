package dev.aurelium.aureliummobs.entities;

import dev.aurelium.aureliummobs.AureliumMobs;
import dev.aurelium.aureliummobs.util.ColorUtils;
import dev.aurelium.aureliummobs.util.MessageUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AureliumMob {

    public AureliumMob(Monster mob, int level, AureliumMobs plugin) {
        if (mob instanceof Zombie){
            mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().clear();
        }

        int level1;
        if (level > 0) {
            level1 = level;
        }
        else {
            level1 = 1;
        }
        Location mobloc = mob.getLocation();
        Location spawnpoint = mob.getWorld().getSpawnLocation();
        double distance = mobloc.distance(spawnpoint);
        double startDamage = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        double startHealth = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        String damageFormula = MessageUtils.setPlaceholders(null, plugin.optionString("mob_defaults.damage.formula")
                .replace("{mob_damage}", String.valueOf(startDamage))
                .replace("{level}", String.valueOf(level1))
                .replace("{distance}", Double.toString(distance))
        );
        String healthFormula = MessageUtils.setPlaceholders(null, plugin.optionString("mob_defaults.health_formula")
                .replace("{mob_health}", String.valueOf(startHealth))
                .replace("{level}", Integer.toString(level1))
                .replace("{distance}", Double.toString(distance))
        );
        Expression resDamage = new ExpressionBuilder(damageFormula).build();
        Expression resHealth = new ExpressionBuilder(healthFormula).build();
        double damage = BigDecimal.valueOf(resDamage.evaluate()).setScale(2, RoundingMode.CEILING).doubleValue();
        double health = resHealth.evaluate();
        String formattedHealth = plugin.getFormatter().format(health);
        if (health > plugin.getMaxHealth()) {
            health = plugin.getMaxHealth();
        }
        if (damage > plugin.getMaxDamage()) {
            damage = plugin.getMaxDamage();
        }
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        mob.setHealth(health);
        mob.getPersistentDataContainer().set(plugin.getMobKey(), PersistentDataType.INTEGER, level);
        if (plugin.isNamesEnabled()) {
            mob.setCustomName(ColorUtils.colorMessage(plugin.optionString("custom_name.format")
                    .replace("{mob}", plugin.getMsg("mobs." + mob.getType().name().toLowerCase()))
                    .replace("{lvl}", String.valueOf(level1))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", formattedHealth)
                    .replace("{distance}", Double.toString(distance))
            ));
            mob.setCustomNameVisible(false);
        }
    }

}
