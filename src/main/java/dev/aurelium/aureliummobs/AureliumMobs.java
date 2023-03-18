package dev.aurelium.aureliummobs;

import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.polyglot.Polyglot;
import com.archyx.polyglot.config.PolyglotConfig;
import com.archyx.polyglot.config.PolyglotConfigBuilder;
import com.archyx.polyglot.lang.MessageKey;
import dev.aurelium.aureliummobs.api.AureliumMobsAPI;
import dev.aurelium.aureliummobs.api.WorldGuardHook;
import dev.aurelium.aureliummobs.commands.AureliumMobsCommand;
import dev.aurelium.aureliummobs.config.ConfigManager;
import dev.aurelium.aureliummobs.config.OptionKey;
import dev.aurelium.aureliummobs.config.OptionValue;
import dev.aurelium.aureliummobs.listeners.*;
import dev.aurelium.aureliummobs.util.Formatter;
import dev.aurelium.aureliummobs.util.Metrics;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;

public class AureliumMobs extends JavaPlugin {

    private static final int bstatsId = 12142;
    private NamespacedKey mobKey;
    private WorldGuardHook worldGuard;
    private AureliumSkills skillsInstance;
    private double maxHealth;
    private double maxDamage;
    private boolean namesEnabled;
    private int globalLevel;
    private Formatter formatter;
    private ConfigManager configManager;
    private Polyglot polyglot;
    private Locale language;
    private List<String> enabledWorlds;
    private boolean worldWhitelist;
    private boolean placeholderAPIEnabled;

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuard = new WorldGuardHook(true);
        }
    }

    @Override
    public void onEnable() {
        AureliumMobsAPI.setPlugin(this);
        // Set Aurelium Skills instance
        Plugin pluginSkills = getServer().getPluginManager().getPlugin("AureliumSkills");
        if (pluginSkills instanceof AureliumSkills) {
            skillsInstance = (AureliumSkills) pluginSkills;
        }

        globalLevel = 0;
        // Load config
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        // Load messages
        PolyglotConfig polyglotConfig = new PolyglotConfigBuilder()
                .defaultLanguage("en")
                .messageDirectory("messages")
                .messageFileName("messages_{language}.yml").build();
        polyglot = new Polyglot(this, polyglotConfig);
        polyglot.getMessageManager().loadMessages();

        for (Player player: this.getServer().getOnlinePlayers()) {
            globalLevel += getLevel(player);
        }
        language = new Locale(optionString("language"));
        mobKey = new NamespacedKey(this, "isAureliumMob");
        namesEnabled = optionBoolean("custom_name.enabled");
        this.getServer().getPluginManager().registerEvents(new MobSpawn(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityXpGainListener(this), this);
        if (namesEnabled) {
            this.getServer().getPluginManager().registerEvents(new MobDamage(this), this);
            this.getServer().getPluginManager().registerEvents(new MobTransform(this), this);
            this.getServer().getPluginManager().registerEvents(new PlayerJoinLeave(this), this);
            if (optionBoolean("custom_name.display_by_range")) {
                this.getServer().getPluginManager().registerEvents(new MoveEvent(this), this);
            }
        }

        new Metrics(this, bstatsId);

        getServer().getPluginManager().registerEvents(new MobDeath(this), this);

        registerCommands();
        loadWorlds();
        maxHealth = Bukkit.spigot().getConfig().getDouble("settings.attribute.maxHealth.max");
        maxDamage = Bukkit.spigot().getConfig().getDouble("settings.attribute.attackDamage.max");

        formatter = new Formatter(optionInt("custom_name.health_rounding_places"));
        // Check for PlaceholderAPI
        placeholderAPIEnabled = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @Override
    public void onDisable() {
    }

    public void loadWorlds() {
        enabledWorlds = optionList("worlds.list");
        worldWhitelist = optionString("worlds.type").equalsIgnoreCase("whitelist");
    }

    public void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new AureliumMobsCommand(this));
    }

    public AureliumSkills getSkillsInstance() {
        return skillsInstance;
    }

    public boolean isNamesEnabled() {
        return namesEnabled;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getMaxDamage() {
        return maxDamage;
    }

    public int getSumLevel(Player p) {
        int result = 0;

        for (Skills s : Skills.values()) {
            result+= AureliumAPI.getSkillLevel(p, s);
        }

        return result;
    }

    public int getAverageLevel(Player p) {
        return getSumLevel(p) / Skills.values().length;
    }

    public int getGlobalLevel() {
        return globalLevel;
    }

    public void setGlobalLevel(int globalLevel) {
        this.globalLevel = globalLevel;
    }

    public int getLevel(Player p) {

        String formula = optionString("player_level.formula")
                .replace("{sumall}", Integer.toString(getSumLevel(p)))
                .replace("{average}", Integer.toString(getAverageLevel(p)))
                .replace("{skillcount}", Integer.toString(Skills.values().length));

        for (Skills s: Skills.values()){
            String replace = "{" + s.name().toLowerCase() + "}";
            formula = formula.replace(replace, Integer.toString(AureliumAPI.getSkillLevel(p, s)));
        }

        return (int) Math.round(new ExpressionBuilder(formula).build().evaluate());
    }

    public boolean isAureliumMob(Monster m) {
        return m.getPersistentDataContainer().has(mobKey, PersistentDataType.INTEGER);
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public List<String> getEnabledWorlds() {
        return enabledWorlds;
    }

    public boolean isWorldWhitelist() {
        return worldWhitelist;
    }

    public WorldGuardHook getWorldGuard() {
        return worldGuard;
    }

    public NamespacedKey getMobKey() {
        return mobKey;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    // Message and config convenience methods
    public String getMsg(String key) {
        return polyglot.getMessageManager().get(language, MessageKey.of(key));
    }

    public OptionValue option(String key) {
        return configManager.getOption(new OptionKey(key));
    }

    public String optionString(String key) {
        return option(key).asString();
    }

    public int optionInt(String key) {
        return option(key).asInt();
    }

    public double optionDouble(String key) {
        return option(key).asDouble();
    }

    public boolean optionBoolean(String key) {
        return option(key).asBoolean();
    }

    public List<String> optionList(String key) {
        return option(key).asList();
    }

}
