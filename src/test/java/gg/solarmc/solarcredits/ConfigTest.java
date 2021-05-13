package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.RotatingShopConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class ConfigTest {
    @Test
    void shouldBeEqualToConfig() {
        ConfigManager<RotatingShopConfig> manager = ConfigManager.create(Path.of("src", "test", "resources"), "rotatingshop.yml", RotatingShopConfig.class);
        manager.reloadConfig();
        Config config = new Config(manager);
        config.loadItems();
        final String name = config.getRotatingItems().get(0).getName();

        Assertions.assertEquals("Item", name);
    }
}
