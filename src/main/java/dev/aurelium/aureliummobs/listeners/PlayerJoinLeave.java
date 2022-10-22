package dev.aurelium.aureliummobs.listeners;

import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeave implements Listener {

    private final AureliumMobs plugin;

    public PlayerJoinLeave(AureliumMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.setGlobalLevel(plugin.getGlobalLevel() + plugin.getLevel(event.getPlayer()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        plugin.setGlobalLevel(plugin.getGlobalLevel() - plugin.getLevel(event.getPlayer()));
    }

}
