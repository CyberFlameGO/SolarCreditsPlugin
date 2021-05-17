package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.command.CommandSender;

public class SetCommand implements CreditSubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender.hasPermission("credits.set"))
            helper.validateAndRun(sender, args, (receiver, amount) -> helper.setCredits(sender, receiver, amount));
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
