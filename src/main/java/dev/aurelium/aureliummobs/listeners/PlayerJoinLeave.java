package dev.aurelium.aureliummobs.listeners;

import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AureliumMobs.globalLevel+= AureliumMobs.getInstance().getLevel(event.getPlayer());
    }

    public void onLeave(PlayerQuitEvent event) {
        AureliumMobs.globalLevel-= AureliumMobs.getInstance().getLevel(event.getPlayer());
    }

}
