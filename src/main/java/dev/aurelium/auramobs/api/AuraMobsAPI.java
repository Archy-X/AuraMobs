package dev.aurelium.auramobs.api;

import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AuraMobsAPI {

    private static AuraMobs plugin;

    public static void setPlugin(AuraMobs instance) {
        if (plugin == null) {
            plugin = instance;
        } else {
            throw new IllegalStateException("Plugin instance has already been set");
        }
    }

    /**
     * A method to get the given mobs level
     * @param e - mob to get level of
     * @return the level of given mob, if not AureliumMob returns 1
     */
    public static int getMobLevel(Entity e) {
        if (!(e instanceof LivingEntity m)) {
            return 1;
        }
        if (!plugin.isAuraMob(m)) {
            return 1;
        }
        Integer persistent = m.getPersistentDataContainer().get(plugin.getMobKey(), PersistentDataType.INTEGER);
        if (persistent == null) {
            return 1;
        }
        return persistent;
    }

    public static boolean isAuraMob(Entity entity) {
       if (!(entity instanceof LivingEntity m)) {
           return false;
       }
        return plugin.isAuraMob(m);
    }

    /**
     * A method to get the given mobs health
     * @param e - mob to get health of
     * @return health of given mob, if not a Monster returns 1
     */
    public static double getMobHealth(Entity e) {
        if (!(e instanceof LivingEntity m)) {
            return 1;
        }
        return BigDecimal.valueOf(m.getHealth()).setScale(2, RoundingMode.CEILING).doubleValue();
    }

    /**
     * A method to get the given mobs max health
     * @param e - mob to get max health of
     * @return max health of given mob, if not a Monster returns 1
     */
    public static double getMobMaxHealth(Entity e) {
        if (!(e instanceof LivingEntity m)) {
            return 1;
        }
        return BigDecimal.valueOf(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()).setScale(2, RoundingMode.CEILING).doubleValue();
    }

    /**
     * A method to get the given mobs max health
     * @param e - mob to get damage of
     * @return damage of given mob, if not a Monster returns 1
     */
    public static int getMobDamage(Entity e) {
        if (!(e instanceof LivingEntity m)) {
            return 1;
        }
        return BigDecimal.valueOf(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()).setScale(2, RoundingMode.CEILING).intValue();
    }

}
