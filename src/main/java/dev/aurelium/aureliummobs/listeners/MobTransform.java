package dev.aurelium.aureliummobs.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import dev.aurelium.aureliummobs.entities.AureliumMob;
import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class MobTransform implements Listener {

    @EventHandler
    public void onMutate(EntityTransformEvent event) {

        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        if (!AureliumMob.isAureliumMob(monster)) {
            return;
        }

        if (!(event.getTransformedEntity() instanceof Monster)) {

            event.getTransformedEntity().getPersistentDataContainer().remove(AureliumMobs.mobKey);
            event.getTransformedEntity().setCustomNameVisible(false);
            event.getTransformedEntity().setCustomName(null);

        }

    }

    @EventHandler
    public void onRename(PlayerInteractAtEntityEvent event) {

        if (!(event.getRightClicked() instanceof Monster monster)) {
            return;
        }

        if (!AureliumMob.isAureliumMob(monster)) {
            return;
        }

        if (!event.getPlayer().getInventory().getItem(event.getHand()).getType().equals(Material.NAME_TAG)) {
            return;
        }

        event.getRightClicked().getPersistentDataContainer().remove(AureliumMobs.mobKey);
        event.getRightClicked().setCustomNameVisible(false);

    }


}
