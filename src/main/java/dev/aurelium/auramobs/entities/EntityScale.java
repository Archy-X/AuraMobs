package dev.aurelium.auramobs.entities;

public class EntityScale {

    private final int levelStart;
    private final int levelEnd;
    private final double[] fixed;
    private final double intervalStart;
    private final double intervalEnd;
    private final double chance;

    public EntityScale(int levelStart, int levelEnd, double[] fixed, double intervalStart, double intervalEnd, double chance) {
        this.levelStart = levelStart;
        this.levelEnd = levelEnd;
        this.fixed = fixed;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.chance = chance;
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
}
