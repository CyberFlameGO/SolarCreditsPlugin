package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.ItemConfig;
import gg.solarmc.solarcredits.config.RotatingShopConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import space.arim.dazzleconf.annote.SubSection;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTest {
    @Test
    void shouldBeEqualToConfig() {
        ConfigManager<RotatingShopConfig> manager = ConfigManager.create(Path.of("src", "test", "resources"), "rotatingshop.yml", RotatingShopConfig.class);
        Config config = new Config(manager, null);
        config.loadItems();
        final String name = config.getRotatingItems().get(0).displayName();

        final RotatingShopConfig data = manager.getConfigData();

        Assertions.assertAll(
                () -> assertEquals("ABCxyz", data.tebexSecret()),
                () -> assertEquals("&l&cA Normal Sword", name),
                () -> assertEquals("diamond_sword", data.items().get("1").material())
        );
    }

    @Test
    void itemsShouldLoad() {
        ConfigManager<RotatingShopConfig> manager = ConfigManager.create(Path.of("src", "test", "resources"), "rotatingshop.yml", RotatingShopConfig.class);
        manager.reloadConfig();

        final Map<String, @SubSection ItemConfig> items = manager.getConfigData().items();
        final ItemConfig item = items.get("1");

        Assertions.assertAll(
                () -> assertEquals("diamond_sword", item.material()),
                () -> assertEquals(3.0, item.priceInCredits())
        );
    }
}
