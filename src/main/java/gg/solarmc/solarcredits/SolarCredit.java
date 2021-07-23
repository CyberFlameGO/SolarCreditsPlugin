package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditCommands;
import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.configs.LastRotateConfig;
import gg.solarmc.solarcredits.config.configs.MessageConfig;
import gg.solarmc.solarcredits.config.configs.RotatingShopConfig;
import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import gg.solarmc.solarcredits.placeholder.CreditsBalance;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class SolarCredit extends JavaPlugin {
    private OkHttpClient okHttpClient;
    private Config config;
    private ConfigManager<MessageConfig> messageManager;
    private ConfigManager<LastRotateConfig> lastRotateManager;

    private RotatingShopMenu shop;
    private CommandHelper helper;
    private final Logger LOGGER = LoggerFactory.getLogger(SolarCredit.class);

    @Override
    public void onEnable() {
        okHttpClient = new OkHttpClient();

        // Config
        Path dataFolder = this.getDataFolder().toPath();

        ConfigManager<RotatingShopConfig> shopManager = ConfigManager.create(dataFolder, "rotatingshop.yml", RotatingShopConfig.class);
        messageManager = ConfigManager.create(dataFolder, "messageconfig.yml", MessageConfig.class);
        lastRotateManager = ConfigManager.create(dataFolder, "lastRotateConfig.yml", LastRotateConfig.class);
        shopManager.reloadConfig();
        messageManager.reloadConfig();
        lastRotateManager.reloadConfig();

        helper = new CommandHelper(this, messageManager.getConfigData());
        backupRotatingShop("rotatingshop.yml", "rotatingShop.backup.yml");

        config = new Config(shopManager, messageManager);
        config.loadItems();

        shop = new RotatingShopMenu(this, config, helper).setLastDay(lastRotateManager.getConfigData().lastRotateDay());

        reloadConfig();

        // Place holder Api
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsBalance(this, helper).register();
        } else {
            LOGGER.warn("Could not find PlaceholderAPI! This plugin is required.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Events
        getServer().getPluginManager().registerEvents(new MenuFunctionListener(), this);

        LOGGER.info("SolarCredits Started");
    }

    @Override
    public void onDisable() {
        long lastDay = shop.getLastDay();
        lastRotateManager.writeConfig(() -> lastDay);

        LOGGER.info("Successfully Disabled");
    }

    @Override
    public void reloadConfig() {
        backupRotatingShop("rotatingshop.yml", "rotatingShop.backup.yml");
        messageManager.reloadConfig();
        config.loadItems();
        String TEBEX_SECRET = config.getShopConfig().tebexSecret();
        getCommand("credits").setExecutor(new CreditCommands(this, helper, TEBEX_SECRET));
    }

    private void backupRotatingShop(String filePath, String backupFilePath) {
        // rotatingShop.backup.yml
        Path dataFolder = getDataFolder().toPath();
        Path backupFile = Path.of(dataFolder.toString(), backupFilePath);

        try (FileInputStream fileInputStream = new FileInputStream(dataFolder.resolve(filePath).toFile());
             FileOutputStream fileOutputStream = new FileOutputStream(backupFile.toFile())) {
            fileInputStream.transferTo(fileOutputStream);
            LOGGER.info("rotatingShop backup Complete!");
        } catch (IOException ex) {
            throw new UncheckedIOException("Backup couldn't be created!!", ex);
        }
    }

    public RotatingShopMenu getShop() {
        return shop;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
