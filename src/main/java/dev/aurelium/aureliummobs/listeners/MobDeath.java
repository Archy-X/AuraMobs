package dev.aurelium.aureliummobs.listeners;

import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeath implements Listener {

    private final AureliumMobs plugin;

    public MobDeath(AureliumMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Monster m)) {
            return;
        }

        if (!plugin.isAureliumMob(m)) {
            return;
        }

        m.setCustomNameVisible(false);
        m.setCustomName(null);
    }

}
