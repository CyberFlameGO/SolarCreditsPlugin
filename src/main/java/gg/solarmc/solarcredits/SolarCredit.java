package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.RotatingShopConfig;
import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

public class SolarCredit extends JavaPlugin {
    private OkHttpClient okHttpClient;
    private RotatingShopMenu shop;

    private String TEBEX_SECRET;
    private final String rotatingShopFilePath = "rotatingshop.yml";

    @Override
    public void onEnable() {
        okHttpClient = new OkHttpClient();
        ConfigManager<RotatingShopConfig> manager = ConfigManager.create(this.getDataFolder().toPath(), rotatingShopFilePath, RotatingShopConfig.class);
        TEBEX_SECRET = manager.getConfigData().tebexSecret();

        Config config = new Config(manager);
        config.loadItems();
        shop = new RotatingShopMenu(this, config);

        getLogger().info("SolarCredits Started");
        getCommand("credits").setExecutor(new CreditCommands(this, TEBEX_SECRET));
    }

    @Override
    public void onDisable() {

    }

    public RotatingShopMenu getShop() {
        return shop;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
