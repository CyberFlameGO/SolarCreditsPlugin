package gg.solarmc.solarcredits;

import org.bukkit.Material;

import java.util.List;

public record RotatingItem(String name, Material material, double priceInCredits,
                           String command, String message, String displayName,
                           List<String> lore) {

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public double getPriceInCredits() {
        return priceInCredits;
    }

    public String getCommand() {
        return command;
    }

    public String getMessage() {
        return message;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }
}

