package dev.aurelium.auramobs.listeners;

import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.util.ColorUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

public class MobDamage implements Listener {

    private final AuraMobs plugin;

    public MobDamage(AuraMobs plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMobDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Monster m)) {
            return;
        }

        if (!plugin.isAuraMob(m)) {
            return;
        }

        int level = m.getPersistentDataContainer().getOrDefault(plugin.getMobKey(), PersistentDataType.INTEGER, 0);
        double resHealth = m.getHealth() - e.getDamage();
        String formattedHealth = plugin.getFormatter().format(resHealth);
        try {
            m.setCustomName(ColorUtils.colorMessage(plugin.optionString("custom_name.format")
                    .replace("{mob}", plugin.getMsg("mobs." + m.getType().name().toLowerCase()))
                    .replace("{lvl}", Integer.toString(level))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", plugin.getFormatter().format(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
            ));
        } catch (NullPointerException ex){
            m.setCustomName(ColorUtils.colorMessage(plugin.optionString("custom_name.format")
                    .replace("{mob}", m.getType().name())
                    .replace("{lvl}", Integer.toString(level))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", plugin.getFormatter().format(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
            ));
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Projectile p)) {
            return;
        }

        if (!(p.getShooter() instanceof Monster m)) {
            return;
        }

        if (!plugin.isAuraMob(m)) {
            return;
        }

        e.setDamage(m.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());

    }

}
