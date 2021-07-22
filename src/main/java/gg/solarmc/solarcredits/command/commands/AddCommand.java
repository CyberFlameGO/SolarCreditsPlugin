package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AddCommand implements CreditSubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender.hasPermission("credits.add"))
            helper.validateAndRun(sender, args,
                    (receiver, amount) -> helper.addCredits(sender, receiver, amount));
        else
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this Command");
        return true;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getArgs() {
        return "[Player Name] [Amount]";
    }

    @Override
    public String getDescription() {
        return "Adds credits to a Player";
    }
}
