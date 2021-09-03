package me.often.aureliummobs.entities;

import me.often.aureliummobs.Main;
import me.often.aureliummobs.util.ColorUtils;
import me.often.aureliummobs.util.MessageUtils;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AureliumMob {

    private String name;
    private EntityType type;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack handItem;
    private double maxHealth;
    private int level;
    private double damage;
    private Main plugin;
    Expression resDamage;
    Expression resHealth;

    public AureliumMob (Monster mob, int level, Main plugin) {

        if (mob instanceof Zombie){
            for (AttributeModifier modifier : mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()) {
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().clear();
                break;
            }
        }

        if (level > 0) {
            this.level = level;
        }
        else {
            this.level = 1;
        }
        this.plugin = plugin;
        Location mobloc = mob.getLocation();
        Location spawnpoint = mob.getLocation().getWorld().getSpawnLocation();
        double distance = mobloc.distance(spawnpoint);
        double startDamage = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        double startHealth = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        String damageFormula = MessageUtils.setPlaceholders(null, plugin.getConfigString("settings.damage-formula")
                .replace("{mob_damage}", String.valueOf(startDamage))
                .replace("{level}", String.valueOf(this.level))
                .replace("{distance}", Double.toString(distance))
        );
        String healthFormula = MessageUtils.setPlaceholders(null, plugin.getConfigString("settings.health-formula")
                .replace("{mob_health}", String.valueOf(startHealth))
                .replace("{level}", Integer.toString(this.level))
                .replace("{distance}", Double.toString(distance))
        );
        resDamage = new ExpressionBuilder(damageFormula).build();
        resHealth = new ExpressionBuilder(healthFormula).build();
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
        mob.getPersistentDataContainer().set(Main.mobKey, PersistentDataType.INTEGER, level);
        if (Main.getInstance().isNamesEnabled()) {
            mob.setCustomName(ColorUtils.colorMessage(plugin.getConfigString("settings.name-format")
                    .replace("{mob}", plugin.getConfigString("mobs."+mob.getType().name().toLowerCase()))
                    .replace("{lvl}", String.valueOf(this.level))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", formattedHealth)
                    .replace("{distance}", Double.toString(distance))
            ));
            mob.setCustomNameVisible(false);
        }
    }



    public static boolean isAureliumMob(Monster m) {
        return m.getPersistentDataContainer().has(Main.mobKey, PersistentDataType.INTEGER);
    }
    /*
    public Monster spawnAt(Location loc){
        Monster mob = (Monster) loc.getWorld().spawnEntity(loc, this.type);
        String damageFormula = plugin.getConfigString("settings.damage-formula")
                .replace("{mob_damage}", Double.toString(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()))
                .replace("{level}", Integer.toString(this.level));
        String healthFormula = plugin.getConfigString("settings.health-formula")
                .replace("{mob_health}", Double.toString(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
                .replace("{level}", Integer.toString(this.level)
                );
        resDamage = new ExpressionBuilder(damageFormula).build();
        resHealth = new ExpressionBuilder(healthFormula).build();
        double damage = BigDecimal.valueOf(resDamage.evaluate()).setScale(2, RoundingMode.CEILING).doubleValue();
        double health = BigDecimal.valueOf(resHealth.evaluate()).setScale(2, RoundingMode.CEILING).doubleValue();
        if (health > plugin.getMaxHealth()){
            health = plugin.getMaxHealth();
        }
        if (damage > plugin.getMaxDamage()){
            damage = plugin.getMaxDamage();
        }
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        mob.getPersistentDataContainer().set(Main.mobKey, PersistentDataType.INTEGER, this.level);
        mob.setCustomName(ColorUtils.colorMessage(plugin.getConfigString("settings.name-format")
                .replace("{mob}", plugin.getConfigString("mobs."+mob.getType().name().toLowerCase()))
                .replace("{lvl}", String.valueOf(this.level))
                .replace("{health}", Double.toString(health))
                .replace("{maxhealth}", Double.toString(health))
        ));
        mob.setCustomNameVisible(false);

        return mob;
    }
     */

}
