package me.often.aureliummobs.listeners;

import me.often.aureliummobs.entities.AureliumMob;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeath implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent e){

        if (!(e.getEntity() instanceof Monster m)){
            return;
        }

        if (!AureliumMob.isAureliumMob(m)){
            return;
        }

        m.setCustomNameVisible(false);
        m.setCustomName(null);

    }

}
