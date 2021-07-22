package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetCommand implements CreditSubCommand {

    @Override
    public void execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender.hasPermission("credits.set"))
            helper.validateAndRun(sender, args,
                    (receiver, amount) -> helper.setCredits(sender, receiver, amount));
        else
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this Command");
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getArgs() {
        return "[Player Name] [Amount]";
    }

    @Override
    public String getDescription() {
        return "Sets credits of a Player";
    }
}
