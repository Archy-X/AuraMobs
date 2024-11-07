package dev.aurelium.auramobs.listeners;

import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class MoveEvent implements Listener {

    private final AuraMobs plugin;

    public MoveEvent(AuraMobs plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        int range = plugin.optionInt("custom_name.display_range");

        World fromWorld = e.getFrom().getWorld();
        if (e.getTo() == null) return;
        World toWorld = e.getTo().getWorld();
        if (fromWorld == null || toWorld == null) return;

        List<Entity> from = new ArrayList<>();
        for (Entity mob : fromWorld.getNearbyEntities(e.getFrom(), range, range, range)) {
            if (mob instanceof LivingEntity && plugin.isAuraMob((LivingEntity) mob)) {
                from.add(mob);
            }
        }
        List<Entity> to = new ArrayList<>();
        for (Entity mob : toWorld.getNearbyEntities(e.getTo(), range, range, range)) {
            if (mob instanceof LivingEntity && plugin.isAuraMob((LivingEntity) mob)) {
                to.add(mob);
            }
        }

        to.forEach(mob -> {
            if (!from.contains(mob)) {
                mob.setCustomNameVisible(e.getPlayer().hasLineOfSight(mob));
            }
        });
        from.forEach(mob -> {
            if (!to.contains(mob)) {
                mob.setCustomNameVisible(false);
            }
        });
    }

}
