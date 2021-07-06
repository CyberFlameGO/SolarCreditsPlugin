package gg.solarmc.solarcredits.placeholder;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class CreditsBalance extends PlaceholderExpansion {
    private final SolarCredit plugin;
    private final CommandHelper helper;

    public CreditsBalance(SolarCredit plugin, CommandHelper helper) {
        this.plugin = plugin;
        this.helper = helper;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "credits_balance";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        BigDecimal bal = player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance();
        return helper.formatBigDecimal(bal);
    }
}
