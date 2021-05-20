package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public record ReloadCommand(SolarCredit plugin) implements CreditSubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender.hasPermission("credits.reload")) {
            plugin.reloadConfig();
            helper.getLogger().info("Reloaded Credits Plugin");
            sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
        } else
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this Command");

        return true;
    }

    @Override
    public String getName() {
        return "reload";
    }
}
