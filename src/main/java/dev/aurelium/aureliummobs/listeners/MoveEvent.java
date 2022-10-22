package dev.aurelium.aureliummobs.listeners;

import dev.aurelium.aureliummobs.entities.AureliumMob;
import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class MoveEvent implements Listener {

    private AureliumMobs plugin;

    public MoveEvent(AureliumMobs plugin){
        this.plugin = plugin;
    }



    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        int range = plugin.getConfigInt("settings.display-range");

        List<Entity> from = e.getFrom().getWorld().getNearbyEntities(e.getFrom(), range, range, range).stream()
                .filter(mob -> mob instanceof Monster && AureliumMob.isAureliumMob((Monster) mob)).toList();
        List<Entity> to = e.getTo().getWorld().getNearbyEntities(e.getTo(), range, range, range).stream()
                .filter(mob -> mob instanceof Monster && AureliumMob.isAureliumMob((Monster) mob)).toList();

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
