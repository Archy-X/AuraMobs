package dev.aurelium.auramobs.listeners;

import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeath implements Listener {

    private final AuraMobs plugin;

    public MobDeath(AuraMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Monster m)) {
            return;
        }

        if (!plugin.isAuraMob(m)) {
            return;
        }

        m.setCustomNameVisible(false);
        m.setCustomName(null);
    }

}
