package me.often.aureliummobs.Listeners;

import me.often.aureliummobs.Entities.AureliumMob;
import me.often.aureliummobs.Main.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class MoveEvent implements Listener {

    private Main plugin;

    public MoveEvent(Main plugin){
        this.plugin = plugin;
    }



    @EventHandler
    public void onMove(PlayerMoveEvent e){

        int range = plugin.getConfigInt("settings.display-range");

        List<Entity> from = e.getFrom().getWorld().getNearbyEntities(e.getFrom(), range, range, range).stream()
                .filter(mob -> mob instanceof Monster && AureliumMob.isAureliumMob((Monster) mob)).toList();
        List<Entity> to = e.getTo().getWorld().getNearbyEntities(e.getTo(), range, range, range).stream()
                .filter(mob -> mob instanceof Monster && AureliumMob.isAureliumMob((Monster) mob)).toList();

        to.forEach(mob -> {
            if (!from.contains(mob)){
                mob.setCustomNameVisible(e.getPlayer().hasLineOfSight(mob));
            }
        });
        from.forEach(mob -> {
            if (!to.contains(mob)){
                mob.setCustomNameVisible(false);
            }
        });


    }

}
