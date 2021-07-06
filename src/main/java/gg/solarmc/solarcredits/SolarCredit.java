package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.RotatingShopConfig;
import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import gg.solarmc.solarcredits.placeholder.CreditsBalance;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolarCredit extends JavaPlugin {
    private OkHttpClient okHttpClient;
    private Config config;
    private RotatingShopMenu shop;
    private CommandHelper helper;
    private final Logger LOGGER = LoggerFactory.getLogger(SolarCredit.class);

    @Override
    public void onEnable() {
        helper = new CommandHelper(this);
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsBalance(this, helper).register();
        } else {
            LOGGER.warn("Could not find PlaceholderAPI! This plugin is required.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        okHttpClient = new OkHttpClient();

        ConfigManager<RotatingShopConfig> manager = ConfigManager.create(this.getDataFolder().toPath(), "rotatingshop.yml", RotatingShopConfig.class);
        this.config = new Config(manager);
        config.loadItems();
        shop = new RotatingShopMenu(this, config);

        String TEBEX_SECRET = manager.getConfigData().tebexSecret();
        getCommand("credits").setExecutor(new CreditCommands(this, helper, TEBEX_SECRET));

        LOGGER.info("SolarCredits Started");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {
        config.loadItems();
        String TEBEX_SECRET = config.getConfig().tebexSecret();
        getCommand("credits").setExecutor(new CreditCommands(this, helper, TEBEX_SECRET));
    }

    public RotatingShopMenu getShop() {
        return shop;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
