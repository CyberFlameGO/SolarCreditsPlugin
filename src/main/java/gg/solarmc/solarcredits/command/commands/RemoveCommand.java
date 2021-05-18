package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.command.CommandSender;

public class RemoveCommand implements CreditSubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender.hasPermission("credits.remove"))
            helper.validateAndRun(sender, args, (receiver, amount) -> helper.removeCredits(sender, receiver, amount));
        else
            sender.sendMessage("You don't have permission to use this Command");
        return true;
    }

    @Override
    public String getName() {
        return "remove";
    }
}
