package gg.solarmc.solarcredits;

import gg.solarmc.solarcredits.config.Config;
import gg.solarmc.solarcredits.config.ConfigManager;
import gg.solarmc.solarcredits.config.configs.ItemConfig;
import gg.solarmc.solarcredits.config.configs.RotatingShopConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import space.arim.dazzleconf.annote.SubSection;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Test
    void shouldNotThrowError() {
        Arrays.stream(getItems()).forEach(System.out::println);
    }

    public RotatingItem[] getItems() {
        ConfigManager<RotatingShopConfig> manager = ConfigManager.create(Path.of("src", "test", "resources"), "rotatingshop.yml", RotatingShopConfig.class);
        Config config = new Config(manager, null);
        config.loadItems();
        final List<RotatingItem> rotatingItems = config.getRotatingItems();

        final long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        final int group = ((int) days % (rotatingItems.size() / 4)) * 4;
        return new RotatingItem[]{rotatingItems.get(group), rotatingItems.get(group + 1), rotatingItems.get(group + 2), rotatingItems.get(group + 3)};
    }
}
