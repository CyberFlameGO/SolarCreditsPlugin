package gg.solarmc.solarcredits;

import org.bukkit.Material;

import java.util.List;

public class RotatingItem {
    private final String name;
    private final Material material;
    private final double priceInCredits;
    private final String command;
    private final String message;
    private final String displayName;
    private final List<String> lore;

    public RotatingItem(String name, Material material, double priceInCredits, String command, String message, String displayName, List<String> lore) {
        this.name = name;
        this.material = material;
        this.priceInCredits = priceInCredits;
        this.command = command;
        this.message = message;
        this.displayName = displayName;
        this.lore = lore;
    }

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

