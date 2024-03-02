package dev.aurelium.auramobs.listeners;

import dev.aurelium.auramobs.api.WorldGuardHook;
import dev.aurelium.auramobs.entities.AureliumMob;
import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.util.MessageUtils;
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

    private final AuraMobs plugin;

    public MobSpawn(AuraMobs plugin){
        this.plugin = plugin;
    }

    @EventHandler (ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        try {
            if (e.getEntity() instanceof Boss) {
                return;
            }
            boolean f = false;
            for (String s : plugin.optionList("spawn_reasons")) {
                if (e.getSpawnReason().name().equalsIgnoreCase(s)) f = true;
            }
            if (!f) return;

            if (!(e.getEntity() instanceof Monster monster)) {
                return;
            }

            if (!passWorld(e.getEntity().getWorld())) return;

            if (plugin.getWorldGuard() != null){
                if (!(plugin.getWorldGuard().mobsEnabled(e.getLocation()))) {
                    return;
                }
            }

            List<String> mobs = plugin.optionList("mob_replacements.list");
            String type = plugin.optionString("mob_replacements.type");

            if (type.equalsIgnoreCase("blacklist") && (mobs.contains(e.getEntity().getType().name()) || mobs.contains("*"))) {
                return;
            }
            else if (type.equalsIgnoreCase("whitelist") && (!mobs.contains(e.getEntity().getType().name().toUpperCase()) && !mobs.contains("*"))) {
                return;
            }

            int radius = plugin.optionInt("player_level.check_radius");

            changeMob(monster, radius).runTask(plugin);

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }

    public boolean passWorld(World world) {
        if (plugin.isWorldWhitelist()) {
            if (plugin.getEnabledWorlds().contains("*")) return true;
            for (String enabledworld : plugin.getEnabledWorlds()) {
                if (world.getName().equalsIgnoreCase(enabledworld) || world.getName().startsWith(enabledworld.replace("*", ""))) return true;
            }
            return false;
        }
        else {
            if (plugin.getEnabledWorlds().contains("*")) return false;
            for (String enabledworld : plugin.getEnabledWorlds()) {
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
                Location spawnpoint = monster.getWorld().getSpawnLocation();
                double distance = mobloc.distance(spawnpoint);
                int level;
                String lformula;
                if (players.isEmpty() || sumlevel == 0) {
                    lformula = MessageUtils.setPlaceholders(null, plugin.optionString("mob_level.backup_formula")
                            .replace("{distance}", Double.toString(distance))
                            .replace("{sumlevel_global}", Integer.toString(plugin.getGlobalLevel()))
                            .replace("{location_x}", Double.toString(monster.getLocation().getX()))
                            .replace("{location_y}", Double.toString(monster.getLocation().getY()))
                            .replace("{location_z}", Double.toString(monster.getLocation().getZ()))
                    );
                }
                else {
                    lformula = MessageUtils.setPlaceholders(null, plugin.optionString("mob_level.formula")
                            .replace("{highestlvl}", Integer.toString(maxlevel))
                            .replace("{lowestlvl}", Integer.toString(minlevel))
                            .replace("{sumlevel}", Integer.toString(sumlevel))
                            .replace("{playercount}", Integer.toString(players.size()))
                            .replace("{distance}", Double.toString(distance))
                            .replace("{sumlevel_global}", Integer.toString(plugin.getGlobalLevel()))
                            .replace("{location_x}", Double.toString(monster.getLocation().getX()))
                            .replace("{location_y}", Double.toString(monster.getLocation().getY()))
                            .replace("{location_z}", Double.toString(monster.getLocation().getZ()))
                    );
                }
                level = (int) new ExpressionBuilder(lformula).build().evaluate();
                level = Math.min(level, plugin.optionInt("mob_level.max_level"));
                new AureliumMob(monster, correctLevel(monster.getLocation(), level), plugin);
            }
        };
    }

    public int correctLevel(Location loc, int level) {
        WorldGuardHook wg = plugin.getWorldGuard();
        if (wg == null) {
            return level;
        }

        if (level < wg.getMinLevel(loc)) {
            return wg.getMinLevel(loc);
        } else return Math.min(level, wg.getMaxLevel(loc));
    }

}
