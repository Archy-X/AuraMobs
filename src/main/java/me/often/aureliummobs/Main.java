package me.often.aureliummobs;

import me.often.aureliummobs.api.WorldGuardHook;
import me.often.aureliummobs.commands.AureliumMobsCommand;
import me.often.aureliummobs.listeners.*;
import me.often.aureliummobs.commands.tabcompleters.AureliumMobsCommandTabCompleter;
import me.often.aureliummobs.util.Metrics;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import me.often.aureliummobs.util.Formatter;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class Main extends JavaPlugin {

    public static List<String> enabledworlds;
    public static boolean world_whitelist;
    public static NamespacedKey mobKey;
    public static WorldGuardHook wghook;
    private static Main instance;
    private static double maxHealth;
    private static double maxDamage;
    private static boolean namesEnabled;
    private static Metrics metrics;
    private static final int bstatsId = 12142;
    public static int globalLevel;
    private Formatter formatter;

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            wghook = new WorldGuardHook(true);
        }
    }

    @Override
    public void onEnable() {
        globalLevel = 0;
        this.saveDefaultConfig();
        for (Player player: this.getServer().getOnlinePlayers()) {
            globalLevel+=getLevel(player);
        }
        mobKey = new NamespacedKey(this, "isAureliumMob");
        namesEnabled = getConfigBool("settings.enable-mob-names");
        this.getServer().getPluginManager().registerEvents(new MobSpawn(this), this);
        if (namesEnabled){
            this.getServer().getPluginManager().registerEvents(new MobDamage(this), this);
            this.getServer().getPluginManager().registerEvents(new MobTransform(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerJoinLeave(), this);
            if (getConfigBool("settings.display-by-range")){
                this.getServer().getPluginManager().registerEvents(new MoveEvent(this), this);
            }
        }

        metrics = new Metrics(this, bstatsId);

        getServer().getPluginManager().registerEvents(new MobDeath(), this);
        /*try {
            this.initConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        instance = this;
        initCommands();
        loadWorlds();
        maxHealth = Bukkit.spigot().getConfig().getDouble("settings.attribute.maxHealth.max");
        maxDamage = Bukkit.spigot().getConfig().getDouble("settings.attribute.attackDamage.max");

        formatter = new Formatter(getConfigInt("settings.health-format-max-places"));
    }

    @Override
    public void onDisable() {
    }

    public void loadWorlds() {
        enabledworlds = this.getConfigStringList("worlds.list");
        world_whitelist = this.getConfigString("worlds.type").equalsIgnoreCase("whitelist");
    }

    public void initCommands() {
        getCommand("aureliummobs").setExecutor(new AureliumMobsCommand());
        getCommand("aureliummobs").setTabCompleter(new AureliumMobsCommandTabCompleter());
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

    public static Main getInstance() {
        return instance;
    }

    public String getConfigString(String path) {
        return this.getConfig().getString(path);
    }

    public List<String> getConfigStringList(String path) {
        return this.getConfig().getStringList(path);
    }

    public int getConfigInt(String path) {
        return this.getConfig().getInt(path);

    }

    /*public void initConfig() throws IOException {
        File oldCfg = new File(this.getDataFolder(), "config_old.yml");
        File cfg = new File(this.getDataFolder(), "config.yml");
        if (!cfg.exists()){
            this.saveDefaultConfig();
        }
        else {
            if (oldCfg.exists()){
                oldCfg.delete();
            }
            cfg.renameTo(oldCfg);
            Files.copy(this.getResource("config.yml"), cfg.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.reloadConfig();
            YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config_old.yml"));
            for (String key : oldConfig.getKeys(true)) {
                if (this.getConfig().get(key) != null && !(this.getConfig().get(key) instanceof ConfigurationSection)){
                    this.getConfig().set(key, oldConfig.get(key));
                }
            }
            System.out.println((this.getConfig().saveToString()));
            PrintWriter printWriter = new PrintWriter(new File(this.getDataFolder(), "config.yml"));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }*/

    public boolean getConfigBool(String path) {
        return this.getConfig().getBoolean(path);
    }

    public int getSumLevel(Player p) {

        int result = 0;

        for (Skills s: Skills.values()) {
            result+= AureliumAPI.getSkillLevel(p, s);
        }

        return result;

    }

    public int getGlobalLevel() {
        return globalLevel;
    }

    public int getLevel(Player p) {

        String formula = getConfigString("settings.player-level-formula")
                .replace("{sumall}", Integer.toString(getSumLevel(p)))
                .replace("{skillcount}", Integer.toString(Skills.values().length));

        for (Skills s: Skills.values()){
            String replace = "{" + s.name().toLowerCase() + "}";
            formula = formula.replace(replace, Integer.toString(AureliumAPI.getSkillLevel(p, s)));
        }

        int level = (int) new ExpressionBuilder(formula).build().evaluate();

        return level;

    }

    public Formatter getFormatter() {
        return formatter;
    }
    
}
