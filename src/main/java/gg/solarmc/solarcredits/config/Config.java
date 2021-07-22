package gg.solarmc.solarcredits.config;

import gg.solarmc.solarcredits.RotatingItem;
import org.bukkit.Material;
import space.arim.dazzleconf.annote.SubSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private final ConfigManager<RotatingShopConfig> shopManager;
    private final ConfigManager<MessageConfig> messageManager;
    private List<RotatingItem> rotatingItems;

    public Config(ConfigManager<RotatingShopConfig> shopManager, ConfigManager<MessageConfig> messageManager) {
        this.shopManager = shopManager;
        this.messageManager = messageManager;
    }

    public void loadItems() {
        shopManager.reloadConfig();
        rotatingItems = new ArrayList<>();

        final List<Map.Entry<String, @SubSection ItemConfig>> items = shopManager.getConfigData().items().entrySet()
                .stream().sorted(Map.Entry.comparingByKey()).toList();

        items.forEach(it -> {
            ItemConfig value = it.getValue();

            Material material = Material.matchMaterial(value.material());
            double price = value.priceInCredits();
            String command = value.command();
            String message = value.message();
            String displayName = value.displayName();
            List<String> lore = value.lore();

            if (value.material().isEmpty()
                    || price == -1
                    || command.isEmpty()
                    || message.isEmpty()) {
                throw new NullPointerException("Missing key from " + it + " in rotatingshop.yml");
            }

            final RotatingItem item = new RotatingItem(material, price, command, message, displayName, lore);
            rotatingItems.add(item);
        });
    }

    public RotatingShopConfig getShopConfig() {
        return shopManager.getConfigData();
    }

    public MessageConfig getMessageConfig() {
        return messageManager.getConfigData();
    }

    public List<RotatingItem> getRotatingItems() {
        return rotatingItems;
    }

}
