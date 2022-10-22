package dev.aurelium.aureliummobs.api;

import dev.aurelium.aureliummobs.AureliumMobs;
import dev.aurelium.aureliummobs.entities.AureliumMob;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AureliumMobsAPI {

    /**
     * A method to get the given mobs level
     * @param e - mob to get level of
     * @return the level of given mob, if not AureliumMob returns 1
     */
    public static int getMobLevel(Entity e) {
        if (!(e instanceof Monster)) {
            return 1;
        }
        Monster m = (Monster) e;
        if (!AureliumMob.isAureliumMob(m)) {
            return 1;
        }
        String persistent = m.getPersistentDataContainer().get(AureliumMobs.mobKey, PersistentDataType.STRING);
        if (persistent == null || persistent.isEmpty()) {
            return 1;
        }
        return Integer.parseInt(persistent);
    }

    /**
     * A method to get the given mobs health
     * @param e - mob to get health of
     * @return health of given mob, if not a Monster returns 1
     */
    public static double getMobHealth(Entity e) {
        if (!(e instanceof Monster m)) {
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
        if (!(e instanceof Monster m)) {
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
        if (!(e instanceof Monster m)) {
            return 1;
        }
        return BigDecimal.valueOf(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()).setScale(2, RoundingMode.CEILING).intValue();
    }

}
