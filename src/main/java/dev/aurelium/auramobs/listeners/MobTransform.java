package dev.aurelium.auramobs.listeners;

import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MobTransform implements Listener {

    private final AuraMobs plugin;

    public MobTransform(AuraMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMutate(EntityTransformEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!plugin.isAuraMob(entity)) {
            return;
        }

        if (!(event.getTransformedEntity() instanceof LivingEntity)) {
            event.getTransformedEntity().getPersistentDataContainer().remove(plugin.getMobKey());
            event.getTransformedEntity().setCustomNameVisible(false);
            event.getTransformedEntity().setCustomName(null);
        }
    }

    @EventHandler
    public void onRename(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity entity)) {
            return;
        }

        if (!plugin.isAuraMob(entity)) {
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
