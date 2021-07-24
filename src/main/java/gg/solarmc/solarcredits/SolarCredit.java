package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditCommands;
import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.configs.*;
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
import java.util.*;
import java.util.stream.Collectors;

public class SolarCredit extends JavaPlugin {
    private OkHttpClient okHttpClient;
    private Config config;
    private ConfigManager<MessageConfig> messageManager;
    private ConfigManager<LastRotateFile> lastRotateManager;

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
        lastRotateManager = ConfigManager.create(dataFolder, "lastRotate.yml", LastRotateFile.class);
        shopManager.reloadConfig();
        messageManager.reloadConfig();
        lastRotateManager.reloadConfig();

        helper = new CommandHelper(this, messageManager.getConfigData());
        backupRotatingShop("rotatingshop.yml", "rotatingShop.backup.yml");

        config = new Config(shopManager, messageManager);
        config.loadItems();

        final LastRotateFile lastRotateData = lastRotateManager.getConfigData();
        final List<Set<UUID>> playersInteracted = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) playersInteracted.add(new HashSet<>());

        lastRotateData.playersInteracted()
                .forEach((key, value) ->
                        playersInteracted.set(Integer.parseInt(key),
                                value.playerUUIDs().stream().map(UUID::fromString).collect(Collectors.toSet())));

        shop = new RotatingShopMenu(this, config, helper)
                .setLastDay(lastRotateData.lastRotateDay())
                .setPlayersInteracted(playersInteracted);

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
        final List<Set<UUID>> players = shop.getPlayersInteracted();

        final Map<String, PlayersInteracted> playersInteracted = Map.of(
                "0", () -> convertUUIDs(players.get(0)),
                "1", () -> convertUUIDs(players.get(1)),
                "2", () -> convertUUIDs(players.get(2)),
                "3", () -> convertUUIDs(players.get(3))
        );

        lastRotateManager.writeConfig(new LastRotateFileImpl(lastDay, playersInteracted));

        LOGGER.info("Successfully Disabled");
    }

    private List<String> convertUUIDs(Set<UUID> list) {
        return list.stream().map(UUID::toString).toList();
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
