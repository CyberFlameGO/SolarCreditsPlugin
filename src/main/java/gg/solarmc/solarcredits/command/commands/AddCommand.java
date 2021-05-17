package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.command.CommandSender;

public class AddCommand implements CreditSubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender.hasPermission("credits.add"))
            helper.validateAndRun(sender, args, (receiver, amount) -> helper.addCredits(sender, receiver, amount));
        return true;
    }

    @Override
    public String getName() {
        return "add";
    }
}
