package gg.solarmc.solarcredits;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataManager {
    private final SolarCredit plugin;
    private FileConfiguration config;
    private File configFile;

    public DataManager(SolarCredit plugin) {
        this.plugin = plugin;
        // saves/initializes new config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), "rotatingshop.yml");

        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource("rotatingshop.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.config.setDefaults(defaultConfig);
        }
        if (plugin.shop != null)
            plugin.shop.loadItems();
    }

    public FileConfiguration getConfig() {
        if (this.config == null)
            reloadConfig();
        return this.config;
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
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), "rotatingshop.yml");

        if (!this.configFile.exists())
            this.plugin.saveResource("rotatingshop.yml", false);
    }
}