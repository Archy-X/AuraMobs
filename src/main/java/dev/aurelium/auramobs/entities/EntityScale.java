package dev.aurelium.auramobs.entities;

import org.bukkit.entity.EntityType;

import java.util.List;

public class EntityScale {

    private final int levelStart;
    private final int levelEnd;
    private final double[] fixed;
    private final double intervalStart;
    private final double intervalEnd;
    private final double chance;
    private final List<EntityType> types;

    public EntityScale(int levelStart, int levelEnd, double[] fixed, double intervalStart, double intervalEnd, double chance, List<EntityType> types) {
        this.levelStart = levelStart;
        this.levelEnd = levelEnd;
        this.fixed = fixed;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.chance = chance;
        this.types = types;
    }

    public int getLevelStart() {
        return levelStart;
    }

    public int getLevelEnd() {
        return levelEnd;
    }

    public double[] getFixed() {
        return fixed;
    }

    public double getIntervalStart() {
        return intervalStart;
    }

    public double getIntervalEnd() {
        return intervalEnd;
    }

    public double getChance() {
        return chance;
    }

    public List<EntityType> getTypes() {
        return types;
    }
}
