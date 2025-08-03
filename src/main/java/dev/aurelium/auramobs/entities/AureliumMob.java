package dev.aurelium.auramobs.entities;

import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.util.ColorUtils;
import dev.aurelium.auramobs.util.MessageUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class AureliumMob {

    public AureliumMob(LivingEntity mob, int level, AuraMobs plugin) {
        if (mob instanceof Zombie) {
            mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().clear();
        }

        int level1;
        if (level > 0) {
            level1 = level;
        }
        else {
            level1 = 1;
        }
        mob.getPersistentDataContainer().set(plugin.getLevelKey(), PersistentDataType.INTEGER, level1);
        Location mobloc = mob.getLocation();
        Location spawnpoint = mob.getWorld().getSpawnLocation();
        double distance = mobloc.distance(spawnpoint);
        double startDamage = mob instanceof EnderDragon ? 0 : BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        double startHealth = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        double startSpeed = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        String prefix = plugin.isBossMob(mob) ? "bosses." : "mob_defaults.";
        String damageFormula = MessageUtils.setPlaceholders(null, plugin.optionString(prefix + "damage.formula")
                .replace("{mob_damage}", String.valueOf(startDamage))
                .replace("{level}", String.valueOf(level1))
                .replace("{distance}", Double.toString(distance))
        );
        String healthFormula = MessageUtils.setPlaceholders(null, plugin.optionString(prefix + "health.formula")
                .replace("{mob_health}", String.valueOf(startHealth))
                .replace("{level}", Integer.toString(level1))
                .replace("{distance}", Double.toString(distance))
        );
        String speedFormula = MessageUtils.setPlaceholders(null, plugin.optionString(prefix + "speed.formula")
                .replace("{mob_speed}", String.valueOf(startSpeed))
                .replace("{level}", Integer.toString(level1))
                .replace("{distance}", Double.toString(distance))
        );
        Expression resDamage = new ExpressionBuilder(damageFormula).build();
        Expression resHealth = new ExpressionBuilder(healthFormula).build();
        Expression resSpeed = new ExpressionBuilder(speedFormula).build();
        double damage = BigDecimal.valueOf(resDamage.evaluate()).setScale(2, RoundingMode.CEILING).doubleValue();
        double health = resHealth.evaluate();
        double speed = resSpeed.evaluate();

        String optDamageMax = plugin.optionString(prefix + "damage.max");
        if (optDamageMax != null && !optDamageMax.isEmpty()) {
            String damageMax = MessageUtils.setPlaceholders(null, optDamageMax
                    .replace("{mob_damage}", String.valueOf(startDamage))
                    .replace("{level}", String.valueOf(level1))
                    .replace("{distance}", Double.toString(distance))
            );
            Expression resMaxDamage = new ExpressionBuilder(damageMax).build();
            double maxDamage = BigDecimal.valueOf(resMaxDamage.evaluate()).setScale(2, RoundingMode.CEILING).doubleValue();
            damage = Math.min(maxDamage, damage);
        }
        String optHealthMax = plugin.optionString(prefix + "health.max");
        if (optHealthMax != null && !optHealthMax.isEmpty()) {
            String healthMax = MessageUtils.setPlaceholders(null, optHealthMax
                    .replace("{mob_health}", String.valueOf(startHealth))
                    .replace("{level}", String.valueOf(level1))
                    .replace("{distance}", Double.toString(distance))
            );
            Expression resMaxHealth = new ExpressionBuilder(healthMax).build();
            double maxHealth = resMaxHealth.evaluate();
            health = Math.min(maxHealth, health);
        }
        String optSpeedMax = plugin.optionString(prefix + "speed.max");
        if (optSpeedMax != null && !optSpeedMax.isEmpty()) {
            String speedMax = MessageUtils.setPlaceholders(null, optSpeedMax
                    .replace("{mob_speed}", String.valueOf(startSpeed))
                    .replace("{level}", String.valueOf(level1))
                    .replace("{distance}", Double.toString(distance))
            );
            Expression resMaxSpeed = new ExpressionBuilder(speedMax).build();
            double maxSpeed = resMaxSpeed.evaluate();
            speed = Math.min(maxSpeed, speed);
        }

        String formattedHealth = plugin.getFormatter().format(health);
        if (health > plugin.getMaxHealth()) {
            health = plugin.getMaxHealth();
        }
        if (damage > plugin.getMaxDamage()) {
            damage = plugin.getMaxDamage();
        }
        if (speed > plugin.getMaxSpeed()) {
            speed = plugin.getMaxSpeed();
        }

        if (!(mob instanceof EnderDragon)) {
            mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
            mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
        }

        AttributeInstance healthAttr = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttr == null) return;
        healthAttr.setBaseValue(health);

        double maxValue = healthAttr.getValue();
        mob.setHealth(Math.min(health, maxValue));

        mob.getPersistentDataContainer().set(plugin.getMobKey(), PersistentDataType.INTEGER, level1);
        if (plugin.isNamesEnabled()) {
            mob.setCustomName(ColorUtils.colorMessage(plugin.optionString("custom_name.format")
                    .replace("{mob}", plugin.getMsg("mobs." + mob.getType().name().toLowerCase(Locale.ROOT)))
                    .replace("{lvl}", String.valueOf(level1))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", formattedHealth)
                    .replace("{distance}", Double.toString(distance))
            ));
            mob.setCustomNameVisible(false);
        }

        if (plugin.getScaleManager().hasScaleAttribute()) {
            plugin.getScaleManager().applyScale(mob, level1);
        }
    }

}
