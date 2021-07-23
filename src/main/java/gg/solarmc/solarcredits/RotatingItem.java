package gg.solarmc.solarcredits;

import org.bukkit.Material;

import java.util.List;

public record RotatingItem(String key, Material material, double priceInCredits,
                           String command, String message, String displayName,
                           List<String> lore) {
    public RotatingItem {
        lore = List.copyOf(lore);
    }
}

