package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.command.CommandSender;

public record ReloadCommand(SolarCredit plugin) implements CreditSubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender.hasPermission("credits.reload")) {
            plugin.reloadConfig();
            LOGGER.info("Reloaded Credits Plugin");
        } else
            sender.sendMessage("You don't have permission to reload this plugin");

        return true;
    }

    @Override
    public String getName() {
        return "reload";
    }
}
