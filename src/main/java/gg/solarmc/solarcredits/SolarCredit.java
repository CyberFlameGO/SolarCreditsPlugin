package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SolarCredit extends JavaPlugin {
    public RotatingShopMenu shop;
    public DataManager manager;

    @Override
    public void onEnable() {
        manager = new DataManager(this);
        shop = new RotatingShopMenu(this);
        getLogger().info("SolarCredits Started");
        getCommand("credits").setExecutor(new CreditCommands(this));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public FileConfiguration getConfig() {
        return manager.getConfig();
    }

}
