package gg.solarmc.solarcredits.command;

import org.bukkit.command.CommandSender;

public interface CreditSubCommand {

    boolean execute(CommandSender sender, String[] args, CommandHelper helper);

    String getName();

}
