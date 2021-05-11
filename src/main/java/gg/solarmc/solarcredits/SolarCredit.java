package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import okhttp3.OkHttpClient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SolarCredit extends JavaPlugin {
    private OkHttpClient okHttpClient;
    private DataManager manager;
    private RotatingShopMenu shop;
    private String TEBEX_SECRET;

    @Override
    public void onEnable() {
        okHttpClient = new OkHttpClient();
        manager = new DataManager(this);
        shop = new RotatingShopMenu(this);
        TEBEX_SECRET = this.getConfig().getString("tebex.secret");

        getLogger().info("SolarCredits Started");
        getCommand("credits").setExecutor(new CreditCommands(this, TEBEX_SECRET));
    }

    @Override
    public void onDisable() {

    }

    public RotatingShopMenu getShop() {
        return shop;
    }

    @Override
    public FileConfiguration getConfig() {
        return manager.getRotatingShop();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
