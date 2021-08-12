package gg.solarmc.solarcredits;

import org.bukkit.Material;

import java.util.List;

public record RotatingItem(String key, Material material, double priceInCredits,
                           List<String> commands, String message, String displayName,
                           List<String> lore) {
    public RotatingItem {
        commands = List.copyOf(commands);
        lore = List.copyOf(lore);
    }
}

