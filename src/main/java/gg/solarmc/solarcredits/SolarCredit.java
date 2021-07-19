package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditCommands;
import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.MessageConfig;
import gg.solarmc.solarcredits.config.RotatingShopConfig;
import gg.solarmc.solarcredits.menus.RotatingShopMenu;
import gg.solarmc.solarcredits.placeholder.CreditsBalance;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public class SolarCredit extends JavaPlugin {
    private OkHttpClient okHttpClient;
    private Config config;
    private RotatingShopMenu shop;
    private CommandHelper helper;
    private ConfigManager<MessageConfig> messageManager;
    private final Logger LOGGER = LoggerFactory.getLogger(SolarCredit.class);

    @Override
    public void onEnable() {
        //backupRotatingShop("rotatingshop.yml", "rotatingShop.backup.yml");

        Path dataFolder = this.getDataFolder().toPath();
        ConfigManager<RotatingShopConfig> shopManager = ConfigManager.create(dataFolder, "rotatingshop.yml", RotatingShopConfig.class);
        messageManager = ConfigManager.create(dataFolder, "messageconfig.yml", MessageConfig.class);
        messageManager.reloadConfig();
        helper = new CommandHelper(this, messageManager.getConfigData());

        this.config = new Config(shopManager, messageManager);
        config.loadItems();
        shop = new RotatingShopMenu(this, config);

        reloadConfig();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CreditsBalance(this, helper).register();
        } else {
            LOGGER.warn("Could not find PlaceholderAPI! This plugin is required.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        okHttpClient = new OkHttpClient();

        String TEBEX_SECRET = shopManager.getConfigData().tebexSecret();
        getCommand("credits").setExecutor(new CreditCommands(this, helper, TEBEX_SECRET));

        LOGGER.info("SolarCredits Started");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {
        messageManager.reloadConfig();
        config.loadItems();
        String TEBEX_SECRET = config.getShopConfig().tebexSecret();
        getCommand("credits").setExecutor(new CreditCommands(this, helper, TEBEX_SECRET));
    }

    private void backupRotatingShop(String filePath, String backupFilePath) {
        // rotatingShop.backup.yml
        try {
            Path dataFolder = getDataFolder().toPath();
            File backupFile = new File(dataFolder + "/" + backupFilePath);

            if (!backupFile.exists()) {
                if (backupFile.createNewFile()) {
                    LOGGER.info("Created a backup rotatingshop File");
                } else {
                    LOGGER.error("Couldn't create a backup rotatingshop File");
                }
            }

            FileReader fin = new FileReader(dataFolder.resolve(filePath).toFile());
            FileWriter fout = new FileWriter(backupFile, true);
            int c;
            while ((c = fin.read()) != -1) {
                fout.write(c);
            }
            LOGGER.info("rotatingShop backup Complete!");

            fin.close();
            fout.close();
        } catch (FileAlreadyExistsException ignored) {
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
