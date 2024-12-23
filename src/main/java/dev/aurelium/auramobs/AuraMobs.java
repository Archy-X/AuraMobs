package dev.aurelium.auramobs;

import co.aikar.commands.PaperCommandManager;
import com.archyx.polyglot.Polyglot;
import com.archyx.polyglot.PolyglotProvider;
import com.archyx.polyglot.config.PolyglotConfig;
import com.archyx.polyglot.config.PolyglotConfigBuilder;
import com.archyx.polyglot.lang.MessageKey;
import dev.aurelium.auramobs.api.AuraMobsAPI;
import dev.aurelium.auramobs.api.WorldGuardHook;
import dev.aurelium.auramobs.commands.AuraMobsCommand;
import dev.aurelium.auramobs.config.ConfigManager;
import dev.aurelium.auramobs.config.OptionKey;
import dev.aurelium.auramobs.config.OptionValue;
import dev.aurelium.auramobs.listeners.*;
import dev.aurelium.auramobs.util.Formatter;
import dev.aurelium.auramobs.entities.ScaleManager;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.user.SkillsUser;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AuraMobs extends JavaPlugin implements PolyglotProvider {

    private static final int bstatsId = 22266;
    private NamespacedKey mobKey;
    private WorldGuardHook worldGuard;
    private AuraSkillsApi auraSkills;
    private double maxHealth;
    private double maxDamage;
    private boolean namesEnabled;
    private int globalLevel;
    private Formatter formatter;
    private ConfigManager configManager;
    private Polyglot polyglot;
    private Locale language;
    private ScaleManager scaleManager;
    private List<String> enabledWorlds;
    private boolean worldWhitelist;
    private boolean placeholderAPIEnabled;
    private boolean mythicMobsEnabled;
    private boolean ignoreMythicMobs;
    private Set<String> spawnReasons;

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuard = new WorldGuardHook(true);
        }
    }

    @Override
    public void onEnable() {
        AuraMobsAPI.setPlugin(this);
        // Set Aurelium Skills instance
        auraSkills = AuraSkillsApi.get();

        globalLevel = 0;
        // Load config
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        // Load messages
        PolyglotConfig polyglotConfig = new PolyglotConfigBuilder()
                .defaultLanguage("en")
                .providedLanguages(new String[] {"en", "es", "de", "nl"})
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
        scaleManager = new ScaleManager(this);
        scaleManager.loadConfiguration();
        ignoreMythicMobs = optionBoolean("custom_name.ignore_mythic_mobs");

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

        this.getServer().getPluginManager().registerEvents(new MobDeath(this), this);

        new Metrics(this, bstatsId);

        registerCommands();
        loadWorlds();
        maxHealth = Bukkit.spigot().getConfig().getDouble("settings.attribute.maxHealth.max");
        maxDamage = Bukkit.spigot().getConfig().getDouble("settings.attribute.attackDamage.max");

        formatter = new Formatter(optionInt("custom_name.health_rounding_places"));
        // Check for PlaceholderAPI
        placeholderAPIEnabled = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        mythicMobsEnabled = getServer().getPluginManager().isPluginEnabled("MythicMobs");
        spawnReasons = new HashSet<>(optionList("spawn_reasons"));
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
        manager.registerCommand(new AuraMobsCommand(this));
    }

    public AuraSkillsApi getAuraSkills() {
        return auraSkills;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Polyglot getPolyglot() {
        return polyglot;
    }

    public ScaleManager getScaleManager() {
        return scaleManager;
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

    public int getSumLevel(Player player) {
        int sum = 0;

        SkillsUser user = auraSkills.getUser(player.getUniqueId());
        for (Skill skill : getEnabledSkills()) {
            sum += user.getSkillLevel(skill);
        }

        return sum;
    }

    public int getAverageLevel(Player p) {
        int enabled = getEnabledSkills().size();
        if (enabled == 0) {
            enabled = Skills.values().length;
        }
        return getSumLevel(p) / enabled;
    }

    private List<Skill> getEnabledSkills() {
        return auraSkills.getGlobalRegistry().getSkills().stream().filter(Skill::isEnabled).toList();
    }

    public int getGlobalLevel() {
        return globalLevel;
    }

    public void setGlobalLevel(int globalLevel) {
        this.globalLevel = globalLevel;
    }

    public int getLevel(Player p) {
        SkillsUser user = auraSkills.getUser(p.getUniqueId());
        List<Skill> skills = getEnabledSkills();
        String formula = optionString("player_level.formula")
                .replace("{sumall}", Integer.toString(getSumLevel(p)))
                .replace("{average}", Integer.toString(getAverageLevel(p)))
                .replace("{skillcount}", Integer.toString(skills.size()));

        for (Skill skill : skills) {
            String replace = "{" + skill.name().toLowerCase(Locale.ROOT) + "}";
            formula = formula.replace(replace, Integer.toString(user.getSkillLevel(skill)));
        }

        return (int) Math.round(new ExpressionBuilder(formula).build().evaluate());
    }

    public boolean isAuraMob(LivingEntity m) {
        return m.getPersistentDataContainer().has(mobKey, PersistentDataType.INTEGER);
    }

    public boolean isInvalidEntity(Entity entity) {
        if (!(entity instanceof LivingEntity mob)) return true;
        if (entity instanceof Hoglin || entity instanceof Slime) return false; // Mobs that don't inherit from LivingEntity
        return !(entity instanceof Monster) && !isBossMob(mob);
    }

    public boolean isBossMob(LivingEntity entity) {
        return entity instanceof Boss || entity instanceof ElderGuardian;
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

    public boolean ignoreMythicMobs() {
        return ignoreMythicMobs;
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

    public boolean isMythicMobsEnabled() {
        return mythicMobsEnabled;
    }

    public Set<String> getSpawnReasons() {
        return spawnReasons;
    }

    // Message and config convenience methods
    public String getMsg(String key) {
        return polyglot.getMessageManager().get(language, MessageKey.of(key));
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
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

    @Override
    public void logInfo(String message) {
        getLogger().info(message);
    }

    @Override
    public void logWarn(String message) {
        getLogger().warning(message);
    }

    @Override
    public void logSevere(String message) {
        getLogger().severe(message);
    }
}
