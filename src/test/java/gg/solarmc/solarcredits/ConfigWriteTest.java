package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.configs.LastRotateFile;
import gg.solarmc.solarcredits.config.configs.LastRotateFileImpl;
import gg.solarmc.solarcredits.config.configs.PlayersInteracted;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ConfigWriteTest {

    public ConfigManager<LastRotateFile> getConfigManager() {
        return ConfigManager.create(Path.of("src", "test", "resources"), "lastRotateConfig.yml", LastRotateFile.class);
    }

    @Test
    void shouldWriteConfig() {
        final ConfigManager<LastRotateFile> configManager = getConfigManager();
        configManager.reloadConfig();

        final Map<String, PlayersInteracted> map = Map.of(
                "0", () -> List.of("Hello"),
                "1", () -> List.of("kjaadnksjasn"),
                "2", () -> List.of("123kjjnfkjn"),
                "3", () -> List.of("0jsdjkdn")
        );

        final Map<String, PlayersInteracted> map2 = Map.of(
                "0", List::of,
                "1", List::of,
                "2", List::of,
                "3", List::of
        );

        configManager.writeConfig(new LastRotateFileImpl(1, map2));
    }

    @Test
    void shouldReadConfig() {
        final ConfigManager<LastRotateFile> configManager = getConfigManager();
        configManager.reloadConfig();

        Assertions.assertEquals(configManager.getConfigData().lastRotateDay(), 1);
    }

}
