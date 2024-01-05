package me.often.aureliummobs.listeners;

import me.often.aureliummobs.entities.AureliumMob;
import me.often.aureliummobs.Main;
import me.often.aureliummobs.util.MessageUtils;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class MobSpawn implements Listener {

    private Main plugin;

    public MobSpawn(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler (ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        try {
            if (e.getEntity() instanceof Boss) {
                return;
            }
            boolean f = false;
            for (String s : plugin.getConfigStringList("spawn-reasons")) {
                if (e.getSpawnReason().name().equalsIgnoreCase(s)) f = true;
            }
            if (!f) return;

            if (!(e.getEntity() instanceof Monster monster)) {
                return;
            }

            if (!passWorld(e.getEntity().getWorld())) return;

            if (Main.wghook != null){
                if (!(Main.wghook.mobsEnabled(e.getLocation()))) {
                    return;
                }
            }

            List<String> mobs = plugin.getConfigStringList("mob-replacements.list");
            String type = plugin.getConfigString("mob-replacements.type");

            if (type.equalsIgnoreCase("blacklist") && (mobs.contains(e.getEntity().getType().name()) || mobs.contains("*"))) {
                return;
            }
            else if (type.equalsIgnoreCase("whitelist") && (!mobs.contains(e.getEntity().getType().name().toUpperCase()) && !mobs.contains("*"))) {
                return;
            }

            int radius = plugin.getConfigInt("settings.check-radius");

            changeMob(monster, radius).runTask(plugin);

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }

    public boolean passWorld(World world) {

        if (Main.world_whitelist) {
            if (Main.enabledworlds.contains("*")) return true;
            for (String enabledworld : Main.enabledworlds) {
                if (world.getName().equalsIgnoreCase(enabledworld) || world.getName().startsWith(enabledworld.replace("*", ""))) return true;
            }
            return false;
        }
        else {
            if (Main.enabledworlds.contains("*")) return false;
            for (String enabledworld : Main.enabledworlds) {
                if (world.getName().equalsIgnoreCase(enabledworld) || world.getName().startsWith(enabledworld.replace("*", ""))) return false;
            }
            return true;
        }
    }

    public BukkitRunnable changeMob(Monster monster, int radius) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (monster.isDead() || !monster.isValid()) {
                    return;
                }
                int sumlevel = 0;
                int maxlevel = Integer.MIN_VALUE;
                int minlevel = Integer.MAX_VALUE;
                List<Entity> players = monster.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player).toList();
                for (Entity player: players) {
                    int lvl = plugin.getLevel((Player) player);
                    sumlevel+=lvl;
                    if (lvl>maxlevel) {maxlevel = lvl;}
                    if (lvl<minlevel) {minlevel = lvl;}
                }
                Location mobloc = monster.getLocation();
                Location spawnpoint = monster.getLocation().getWorld().getSpawnLocation();
                double distance = mobloc.distance(spawnpoint);
                int level;
                if (players.size() == 0 || sumlevel == 0) {
                    String lformula = MessageUtils.setPlaceholders(null, plugin.getConfigString("settings.default-mob-level-formula")
                            .replace("{world}", monster.getLocation().getWorld().getName())
                            .replace("{distance}", Double.toString(distance))
                            .replace("{sumlevel_global}", Integer.toString(Main.getInstance().getGlobalLevel()))
                            .replace("{location_x}", Double.toString(monster.getLocation().getX()))
                            .replace("{location_y}", Double.toString(monster.getLocation().getY()))
                            .replace("{location_z}", Double.toString(monster.getLocation().getZ()))
                    );
                    level = (int) new ExpressionBuilder(lformula).build().evaluate();
                }
                else {
                    String lformula = MessageUtils.setPlaceholders(null, plugin.getConfigString("settings.mob-level-formula")
                            .replace("{world}", monster.getLocation().getWorld().getName())
                            .replace("{highestlvl}", Integer.toString(maxlevel))
                            .replace("{lowestlvl}", Integer.toString(minlevel))
                            .replace("{sumlevel}", Integer.toString(sumlevel))
                            .replace("{playercount}", Integer.toString(players.size()))
                            .replace("{distance}", Double.toString(distance))
                            .replace("{sumlevel_global}", Integer.toString(Main.getInstance().getGlobalLevel()))
                            .replace("{location_x}", Double.toString(monster.getLocation().getX()))
                            .replace("{location_y}", Double.toString(monster.getLocation().getY()))
                            .replace("{location_z}", Double.toString(monster.getLocation().getZ()))
                    );
                    level = (int) new ExpressionBuilder(lformula).build().evaluate();
                }
                new AureliumMob(monster, correctLevel(monster.getLocation(), level), plugin);
            }
        };
    }

    public int correctLevel(Location loc, int level) {
        if (Main.wghook == null) {
            return level;
        }

        if (level < Main.wghook.getMinLevel(loc)) {
            return Main.wghook.getMinLevel(loc);
        } else if (level > Main.wghook.getMaxLevel(loc)) {
            return Main.wghook.getMaxLevel(loc);
        } else {
             return level;
        }
    }

}
