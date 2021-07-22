package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class BalanceCommand implements CreditSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender instanceof Player player) {
            final BigDecimal balance = player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance();

            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "Your balance is : " + helper.formatBigDecimal(balance));
                return;
            }

        } else
            sender.sendMessage(ChatColor.RED + "Only Players use this Command");
    }

    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getArgs() {
        return "[Player Name/Optional]";
    }

    @Override
    public String getDescription() {
        return "See your ";
    }
}
