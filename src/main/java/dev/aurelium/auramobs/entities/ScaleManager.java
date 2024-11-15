package dev.aurelium.auramobs.entities;

import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScaleManager {

    private final List<EntityScale> entries = new ArrayList<>();
    private final AuraMobs plugin;
    private List<String> enabledWorlds;
    private boolean worldWhitelist;
    private boolean override;
    private Attribute scaleAttribute;

    public ScaleManager(AuraMobs plugin) {
        this.plugin = plugin;
    }

    public void loadConfiguration() {
        try {
            entries.clear();

            enabledWorlds = plugin.optionList("scaling.worlds.list");
            worldWhitelist = plugin.optionString("scaling.worlds.type").equalsIgnoreCase("whitelist");

            for (Attribute attribute : Attribute.values()) {
                if (attribute.name().toLowerCase().contains("scale")) {
                    scaleAttribute = attribute;
                    break;
                }
            }

            if (scaleAttribute == null) return;

            ConfigurationSection section = plugin.getConfig().getConfigurationSection("scaling");
            if (section == null) return;

            ConfigurationSection levelSection = section.getConfigurationSection("levels");
            if (levelSection == null) return;

            override = plugin.optionBoolean("scaling.override");

            for (String entry : levelSection.getKeys(false)) {
                ConfigurationSection entrySection = levelSection.getConfigurationSection(entry);
                if (entrySection == null) continue;

                if (entry.split("-").length < 2) {
                    plugin.getLogger().warning("Scale entry key must be in a range format (e.g. 1-20)");
                    continue;
                }

                int levelStart = Integer.parseInt(entry.split("-")[0]);
                int levelEnd = Integer.parseInt(entry.split("-")[1]);
                double chance = entrySection.getDouble("chance", 1.0);
                double intervalStart = 0;
                double intervalEnd = 0;
                double[] fixed = new double[0];

                String scale = entrySection.getString("scale");

                if (scale == null) {
                    plugin.getLogger().warning("Scale entry " + entry + " is missing the scale value!");
                    continue;
                }

                if (scale.contains("-")) {
                    if (scale.split("-").length != 2) {
                        plugin.getLogger().warning("Scale entry " + entry + " has an invalid scale value!");
                        continue;
                    }

                    intervalStart = Double.parseDouble(scale.split("-")[0]);
                    intervalEnd = Double.parseDouble(scale.split("-")[1]);

                } else {
                    fixed = Arrays.stream(scale.replace(" ", "").split(",")).mapToDouble(Double::parseDouble).toArray();
                }

                List<EntityType> types = new ArrayList<>();
                if (entrySection.contains("types")) {
                    for (String type : entrySection.getStringList("types")) {
                        try {
                            types.add(EntityType.valueOf(type.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid entity type in scale entry " + entry + ": " + type);
                        }
                    }
                }

                entries.add(new EntityScale(levelStart, levelEnd, fixed, intervalStart, intervalEnd, chance, types));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load scale configuration: " + e.getMessage());
        }
    }

    public void applyScale(LivingEntity entity, int level) {
        if (entries.isEmpty()) return;
        if (!passWorld(entity.getWorld())) return;

        AttributeInstance ai = entity.getAttribute(scaleAttribute);
        if (ai == null) return;

        for (EntityScale entry : entries) {
            if (level < entry.getLevelStart() || level > entry.getLevelEnd()) {
                continue;
            }
            if (!entry.getTypes().isEmpty() && !entry.getTypes().contains(entity.getType())) {
                continue;
            }

            if (Math.random() < entry.getChance()) {
                double random;
                if (entry.getFixed().length > 0) {
                    random = entry.getFixed()[ThreadLocalRandom.current().nextInt(entry.getFixed().length)];
                } else {
                    random = entry.getIntervalStart() + (entry.getIntervalEnd() - entry.getIntervalStart()) * Math.random();
                }
                if (!override) {
                    random = random * ai.getValue();
                }
                ai.setBaseValue(Math.max(.00625, Math.min(16, random)));
            }
        }
    }

    public boolean hasScaleAttribute() {
        return scaleAttribute != null;
    }

    private boolean passWorld(World world) {
        if (worldWhitelist) {
            if (enabledWorlds.contains("*")) return true;
            for (String enabledworld : enabledWorlds) {
                if (world.getName().equalsIgnoreCase(enabledworld) || world.getName().startsWith(enabledworld.replace("*", ""))) {
                    return true;
                }
            }
            return false;
        } else {
            if (enabledWorlds.contains("*")) return false;
            for (String enabledworld : enabledWorlds) {
                if (world.getName().equalsIgnoreCase(enabledworld) || world.getName().startsWith(enabledworld.replace("*", ""))) {
                    return false;
                }
            }
            return true;
        }
    }
}
