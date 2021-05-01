package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataManager {
    private final SolarCredit plugin;
    private final String rotatingShopFilePath = "rotatingshop.yml";
    private FileConfiguration rotatingShop;
    private File rotatingShopFile;

    public DataManager(SolarCredit plugin) {
        this.plugin = plugin;
        // saves/initializes new config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.rotatingShopFile == null)
            this.rotatingShopFile = new File(this.plugin.getDataFolder(), rotatingShopFilePath);

        this.rotatingShop = YamlConfiguration.loadConfiguration(this.rotatingShopFile);

        InputStream defaultStream = this.plugin.getResource(rotatingShopFilePath);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.rotatingShop.setDefaults(defaultConfig);
        }

        final RotatingShopMenu shop = plugin.getShop();
        if (shop != null)
            shop.loadItems();
    }

    public FileConfiguration getRotatingShop() {
        if (this.rotatingShop == null)
            reloadConfig();
        return this.rotatingShop;
    }


    /*
    Use if want to save config after Changing it from Commands

    public void saveConfig() {
        if (this.config == null || this.configFile == null)
            return;
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            LOGGER.error("Could not save config to " + this.configFile, e);
        }
    }
    */

    public void saveDefaultConfig() {
        plugin.saveDefaultConfig();

        if (this.rotatingShopFile == null)
            this.rotatingShopFile = new File(this.plugin.getDataFolder(), rotatingShopFilePath);

        if (!this.rotatingShopFile.exists())
            this.plugin.saveResource(rotatingShopFilePath, false);
    }
}