package gg.solarmc.solarcredits.command;

import org.bukkit.command.CommandSender;

public interface CreditSubCommand {

    void execute(CommandSender sender, String[] args, CommandHelper helper);

    String getName();

    String getArgs();

    String getDescription();

}
