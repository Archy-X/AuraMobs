package me.often.aureliummobs.API;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Set;

public record WorldGuardHook(boolean hasWorldGuard) {

    public static Flag<Integer> minMoblevel;
    public static Flag<Integer> maxMoblevel;
    public static Flag<StateFlag.State> mobsEnabled;

    public WorldGuardHook(boolean hasWorldGuard) {

        this.hasWorldGuard = hasWorldGuard;

        if (!hasWorldGuard) {
            return;
        }

        minMoblevel = new IntegerFlag("aureliummobs-min-level");
        maxMoblevel = new IntegerFlag("aureliummobs-max-level");
        mobsEnabled = new StateFlag("aureliummobs-mob-spawning", true);

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            registry.register(minMoblevel);
            registry.register(maxMoblevel);
            registry.register(mobsEnabled);
        } catch (IllegalStateException ignored){}



    }

    public int getMinLevel(Location loc) {

        if (!hasWorldGuard) {
            return -1;
        }

        BlockVector3 position = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));

        Set<ProtectedRegion> regions = manager.getApplicableRegions(position).getRegions();

        if (regions.size() < 1) {
            return -1;
        } else if (new ArrayList<>(regions).get(0).getFlags().containsKey(minMoblevel)) {
            return new ArrayList<>(regions).get(0).getFlag(minMoblevel);
        }

        return -1;

    }

    public int getMaxLevel(Location loc) {

        if (!hasWorldGuard) {
            return Integer.MAX_VALUE;
        }

        BlockVector3 position = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));

        Set<ProtectedRegion> regions = manager.getApplicableRegions(position).getRegions();

        if (regions.size() < 1) {
            return Integer.MAX_VALUE;
        } else if (new ArrayList<>(regions).get(0).getFlags().containsKey(maxMoblevel)) {
            return new ArrayList<>(regions).get(0).getFlag(maxMoblevel);
        }

        return Integer.MAX_VALUE;

    }

    public boolean mobsEnabled(Location loc) {

        if (!hasWorldGuard) {
            return true;
        }

        BlockVector3 position = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));

        Set<ProtectedRegion> regions = manager.getApplicableRegions(position).getRegions();

        if (regions.size() < 1) {
            return true;
        } else if (new ArrayList<>(regions).get(0).getFlags().containsKey(mobsEnabled)) {
            return new ArrayList<>(regions).get(0).getFlag(mobsEnabled).equals(StateFlag.State.ALLOW);
        }

        return true;

    }

}
