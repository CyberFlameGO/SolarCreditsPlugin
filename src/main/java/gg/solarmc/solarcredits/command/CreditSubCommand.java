package gg.solarmc.solarcredits.command;

import gg.solarmc.solarcredits.CreditCommands;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public interface CreditSubCommand {
    Logger LOGGER = LoggerFactory.getLogger(CreditCommands.class);
    DecimalFormat df = new DecimalFormat("#,###.00");

    boolean execute(CommandSender sender, String[] args, CommandHelper helper);

    String getName();

}
