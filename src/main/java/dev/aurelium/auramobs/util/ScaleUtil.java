package dev.aurelium.auramobs.util;

import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class ScaleUtil {

    private static final LinkedList<ScaleEntry> entries = new LinkedList<>();
    private static Attribute scaleAttribute;
    private static AuraMobs plugin;

    private static class ScaleEntry {

        private final int levelStart;
        private final int levelEnd;
        private final double[] fixed;
        private final double intervalStart;
        private final double intervalEnd;
        private final double chance;

        public ScaleEntry(int levelStart, int levelEnd, double[] fixed, double intervalStart, double intervalEnd, double chance) {
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

    public static void initialize(AuraMobs plugin) {
        ScaleUtil.plugin = plugin;
        loadConfiguration();
    }

    public static void loadConfiguration() {

        entries.clear();

        for (Attribute attribute : Attribute.values()) {
            if (attribute.name().toLowerCase().contains("scale")) {
                scaleAttribute = attribute;
                break;
            }
        }

        if(scaleAttribute == null) return;

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("scales");

        for (String entry : section.getKeys(false)) {
            ConfigurationSection entrySection = section.getConfigurationSection(entry);

            int levelStart = Integer.parseInt(entry.split("-")[0]);
            int levelEnd = Integer.parseInt(entry.split("-")[1]);
            double chance = entrySection.getDouble("chance");

            String scale = entrySection.getString("scale");
            if(scale.contains("-")) {
                System.out.println("Interval");
                double intervalStart = Double.parseDouble(scale.split("-")[0]);
                double intervalEnd = Double.parseDouble(scale.split("-")[1]);
                entries.add(new ScaleEntry(levelStart, levelEnd, new double[0], intervalStart, intervalEnd, chance));
            } else {
                System.out.println("Fixed");
                double[] fixed = Arrays.stream(scale.replace(" ", "").split(",")).mapToDouble(Double::parseDouble).toArray();
                entries.add(new ScaleEntry(levelStart, levelEnd, fixed, 0, 0, chance));
            }
        }

        System.out.println(entries);
    }

    public static void applyScale(LivingEntity entity, int level) {
        for (ScaleEntry entry : entries) {
            if (level >= entry.getLevelStart() && level <= entry.getLevelEnd()) {
                if (Math.random() < entry.getChance()) {
                    if (entry.getFixed().length > 0) {
                        entity.getAttribute(scaleAttribute).setBaseValue(Math.max(.00625, Math.min(16, entry.getFixed()[ThreadLocalRandom.current().nextInt(entry.getFixed().length)])));
                    } else {
                        double random = entry.getIntervalStart() + (entry.getIntervalEnd() - entry.getIntervalStart()) * Math.random();
                        entity.getAttribute(scaleAttribute).setBaseValue(Math.max(.00625, Math.min(16, random)));
                    }
                }
            }
        }
    }
}
