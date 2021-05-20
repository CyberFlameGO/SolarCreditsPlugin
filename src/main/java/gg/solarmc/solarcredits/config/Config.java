package gg.solarmc.solarcredits.config;

import gg.solarmc.solarcredits.RotatingItem;
import org.bukkit.Material;
import space.arim.dazzleconf.annote.SubSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private final ConfigManager<RotatingShopConfig> manager;
    private List<RotatingItem> rotatingItems;

    public Config(ConfigManager<RotatingShopConfig> manager) {
        this.manager = manager;
    }

    public void loadItems() {
        rotatingItems = new ArrayList<>();
        manager.reloadConfig();

        Map<String, @SubSection ItemConfig> items = manager.getConfigData().items();

        items.forEach((key, value) -> {
            Material material = Material.matchMaterial(value.material());
            double price = value.priceInCredits();
            String command = value.command();
            String message = value.message();
            final String display = value.displayName();
            String displayName = display.isEmpty() ? key : display;
            List<String> lore = value.lore();

            if (value.material().isEmpty()
                    || price == -1
                    || command.isEmpty()
                    || message.isEmpty()) {
                throw new NullPointerException("Missing key from " + key + " in rotatingshop.yml");
            }

            final RotatingItem item = new RotatingItem(key, material, price, command, message, displayName, lore);
            rotatingItems.add(item);
        });
    }

    public RotatingShopConfig getConfig() {
        return manager.getConfigData();
    }

    public List<RotatingItem> getRotatingItems() {
        return rotatingItems;
    }

}
