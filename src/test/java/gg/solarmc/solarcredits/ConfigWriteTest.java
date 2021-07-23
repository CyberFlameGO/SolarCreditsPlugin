package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.configs.LastRotateConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class ConfigWriteTest {

    public ConfigManager<LastRotateConfig> getConfigManager() {
        return ConfigManager.create(Path.of("src", "test", "resources"), "lastRotateConfig.yml", LastRotateConfig.class);
    }

    @Test
    void shouldWriteConfig() {
        final ConfigManager<LastRotateConfig> configManager = getConfigManager();
        configManager.reloadConfig();

        configManager.writeConfig(() -> 1);
    }

    @Test
    void shouldReadConfig() {
        final ConfigManager<LastRotateConfig> configManager = getConfigManager();
        configManager.reloadConfig();

        Assertions.assertEquals(configManager.getConfigData().lastRotateDay(), 1);
    }

}
