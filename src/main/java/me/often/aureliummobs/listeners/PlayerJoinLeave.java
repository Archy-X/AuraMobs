package me.often.aureliummobs.listeners;

import me.often.aureliummobs.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Main.globalLevel+=Main.getInstance().getLevel(event.getPlayer());
    }

    public void onLeave(PlayerQuitEvent event){
        Main.globalLevel-=Main.getInstance().getLevel(event.getPlayer());
    }

}
