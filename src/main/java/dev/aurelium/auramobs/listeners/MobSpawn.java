package dev.aurelium.auramobs.listeners;

import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.api.WorldGuardHook;
import dev.aurelium.auramobs.entities.AureliumMob;
import dev.aurelium.auramobs.util.MessageUtils;
import io.lumine.mythic.core.constants.MobKeys;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MobSpawn implements Listener {

    private final AuraMobs plugin;
    private final Random random = new Random();

    public MobSpawn(AuraMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSpawn(CreatureSpawnEvent e) {
        try {
            if (!plugin.getSpawnReasons().contains(e.getSpawnReason().name())) return;

            if (plugin.isInvalidEntity(e.getEntity())) {
                return;
            }

            LivingEntity entity = e.getEntity();

            if (!plugin.optionBoolean("bosses.enabled") && plugin.isBossMob(entity)) {
                return;
            }

            if (!passWorld(e.getEntity().getWorld())) return;

            if (plugin.getWorldGuard() != null) {
                if (!(plugin.getWorldGuard().mobsEnabled(e.getLocation()))) {
                    return;
                }
            }

            List<String> mobs = plugin.optionList("mob_replacements.list");
            String type = plugin.optionString("mob_replacements.type");

            if (type.equalsIgnoreCase("blacklist") && (mobs.contains(e.getEntity().getType().name()) || mobs.contains("*"))) {
                return;
            } else if (type.equalsIgnoreCase("whitelist") && (!mobs.contains(e.getEntity().getType().name().toUpperCase(Locale.ROOT)) && !mobs.contains("*"))) {
                return;
            }

            if (!plugin.optionBoolean("custom_name.allow_override")) {
                if (e.getEntity().getCustomName() != null) {
                    return;
                }
            }

            PersistentDataContainer data = entity.getPersistentDataContainer();
            if (data.has(plugin.getSummonKey(), PersistentDataType.BYTE)) {
                return; // Already handled via command
            }

            int radius = plugin.optionInt("player_level.check_radius");

            changeMob(entity, radius).runTask(plugin);
        } catch (NullPointerException ex) {
            plugin.getLogger().severe(ex.getMessage());
        }
    }


    private boolean passWorld(World world) {
        if (plugin.isWorldWhitelist()) {
            if (plugin.getEnabledWorlds().contains("*")) return true;
            for (String enabledworld : plugin.getEnabledWorlds()) {
                if (world.getName().equalsIgnoreCase(enabledworld) || world.getName().startsWith(enabledworld.replace("*", ""))) {
                    return true;
                }
            }
            return false;
        } else {
            if (plugin.getEnabledWorlds().contains("*")) return false;
            for (String enabledworld : plugin.getEnabledWorlds()) {
                if (world.getName().equalsIgnoreCase(enabledworld) || world.getName().startsWith(enabledworld.replace("*", ""))) {
                    return false;
                }
            }
            return true;
        }
    }

    public BukkitRunnable changeMob(LivingEntity entity, int radius) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead() || !entity.isValid()) {
                    return;
                }

                if (plugin.isMythicMobsEnabled() && entity.getPersistentDataContainer().has(MobKeys.TYPE, PersistentDataType.STRING) && plugin.ignoreMythicMobs()) {
                    return;
                }

                int sumlevel = 0;
                int maxlevel = Integer.MIN_VALUE;
                int minlevel = Integer.MAX_VALUE;
                int playercount = 0;

                for (Entity entity : entity.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof Player player) {
                        if (player.hasMetadata("NPC")) continue;
                        int lvl = plugin.getLevel(player);
                        sumlevel += lvl;
                        playercount++;
                        if (lvl > maxlevel) {
                            maxlevel = lvl;
                        }
                        if (lvl < minlevel) {
                            minlevel = lvl;
                        }
                    }
                }
                Location mobloc = entity.getLocation();
                Location spawnpoint = entity.getWorld().getSpawnLocation();
                double distance = mobloc.distance(spawnpoint);
                int level;

                int overrideLevel = getMetadataLevel(entity);
                if (overrideLevel != 0) {
                    level = overrideLevel;
                } else {
                    level = getCalculatedLevel(entity, playercount, distance, maxlevel, minlevel, sumlevel);
                }
                new AureliumMob(entity, correctLevel(entity.getLocation(), level), plugin);
            }
        };
    }

    private int getCalculatedLevel(LivingEntity entity, int playercount, double distance, int maxlevel, int minlevel, int sumlevel) {
        int level;
        String lformula;
        String prefix = plugin.isBossMob(entity) ? "bosses.level." : "mob_level.";
        int globalOnline = plugin.getServer().getOnlinePlayers().size();
        if (playercount == 0) {
            lformula = MessageUtils.setPlaceholders(null, plugin.optionString(prefix + "backup_formula")
                    .replace("{distance}", Double.toString(distance))
                    .replace("{sumlevel_global}", Integer.toString(plugin.getGlobalLevel()))
                    .replace("{playercount}", globalOnline > 0 ? String.valueOf(globalOnline) : "1")
                    .replace("{location_x}", Double.toString(entity.getLocation().getX()))
                    .replace("{location_y}", Double.toString(entity.getLocation().getY()))
                    .replace("{location_z}", Double.toString(entity.getLocation().getZ()))
                    .replace("{random_int}", String.valueOf(random.nextInt(100) + 1))
                    .replace("{random_double}", String.valueOf(random.nextDouble()))
            );
        } else {
            lformula = MessageUtils.setPlaceholders(null, plugin.optionString(prefix + "formula")
                    .replace("{highestlvl}", Integer.toString(maxlevel))
                    .replace("{lowestlvl}", Integer.toString(minlevel))
                    .replace("{sumlevel}", Integer.toString(sumlevel))
                    .replace("{playercount}", Integer.toString(playercount))
                    .replace("{distance}", Double.toString(distance))
                    .replace("{sumlevel_global}", Integer.toString(plugin.getGlobalLevel()))
                    .replace("{location_x}", Double.toString(entity.getLocation().getX()))
                    .replace("{location_y}", Double.toString(entity.getLocation().getY()))
                    .replace("{location_z}", Double.toString(entity.getLocation().getZ()))
                    .replace("{random_int}", String.valueOf(random.nextInt(100) + 1))
                    .replace("{random_double}", String.valueOf(random.nextDouble()))
            );
        }
        level = (int) new ExpressionBuilder(lformula).build().evaluate();
        level = Math.min(level, plugin.optionInt(prefix + "max_level"));
        return level;
    }

    private int getMetadataLevel(Entity entity) {
        int overrideLevel = 0;
        List<MetadataValue> meta = entity.getMetadata("auraskills_level");
        if (!meta.isEmpty()) {
            for (MetadataValue val : meta) {
                Plugin owning = val.getOwningPlugin();
                if (owning == null) continue;

                if (owning.getName().equals("AuraSkills")) {
                    overrideLevel = val.asInt();
                    break;
                }
            }
        }
        return overrideLevel;
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
