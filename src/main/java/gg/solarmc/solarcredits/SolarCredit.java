package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.RotatingShopConfig;
import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

public class SolarCredit extends JavaPlugin {
    private OkHttpClient okHttpClient;
    private Config config;
    private RotatingShopMenu shop;

    @Override
    public void onEnable() {
        okHttpClient = new OkHttpClient();

        ConfigManager<RotatingShopConfig> manager = ConfigManager.create(this.getDataFolder().toPath(), "rotatingshop.yml", RotatingShopConfig.class);
        this.config = new Config(manager);
        config.loadItems();
        shop = new RotatingShopMenu(this, config);
        String TEBEX_SECRET = manager.getConfigData().tebexSecret();
        getCommand("credits").setExecutor(new CreditCommands(this, TEBEX_SECRET));

        getLogger().info("SolarCredits Started");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {
        config.loadItems();
        String TEBEX_SECRET = config.getConfig().tebexSecret();
        getCommand("credits").setExecutor(new CreditCommands(this, TEBEX_SECRET));
    }

    public RotatingShopMenu getShop() {
        return shop;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
