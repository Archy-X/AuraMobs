package dev.aurelium.aureliummobs.listeners;

import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MobTransform implements Listener {

    private final AureliumMobs plugin;

    public MobTransform(AureliumMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMutate(EntityTransformEvent event) {

        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        if (!plugin.isAureliumMob(monster)) {
            return;
        }

        if (!(event.getTransformedEntity() instanceof Monster)) {

            event.getTransformedEntity().getPersistentDataContainer().remove(plugin.getMobKey());
            event.getTransformedEntity().setCustomNameVisible(false);
            event.getTransformedEntity().setCustomName(null);

        }

    }

    @EventHandler
    public void onRename(PlayerInteractAtEntityEvent event) {

        if (!(event.getRightClicked() instanceof Monster monster)) {
            return;
        }

        if (!plugin.isAureliumMob(monster)) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (item != null && !item.getType().equals(Material.NAME_TAG)) {
            return;
        }

        event.getRightClicked().getPersistentDataContainer().remove(plugin.getMobKey());
        event.getRightClicked().setCustomNameVisible(false);

    }


}
