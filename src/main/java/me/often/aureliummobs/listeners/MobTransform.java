package me.often.aureliummobs.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import me.often.aureliummobs.entities.AureliumMob;
import me.often.aureliummobs.Main;
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

            event.getTransformedEntity().getPersistentDataContainer().remove(Main.mobKey);
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

        event.getRightClicked().getPersistentDataContainer().remove(Main.mobKey);
        event.getRightClicked().setCustomNameVisible(false);

    }


}
