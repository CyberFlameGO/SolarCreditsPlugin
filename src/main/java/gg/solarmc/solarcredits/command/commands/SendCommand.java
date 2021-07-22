package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record SendCommand(SolarCredit plugin) implements CreditSubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender instanceof Player player)
            helper.validateAndRun(sender, args,
                    (receiver, amount) -> {
                        helper.sendCredits(player, receiver, amount);
                        receiver.sendMessage(ChatColor.GREEN + "You received " + amount + " from " + sender.getName());
                    });
        else
            sender.sendMessage(ChatColor.RED + "Only players can use this Command");

        return true;
    }

    @Override
    public String getName() {
        return "send";
    }
}
