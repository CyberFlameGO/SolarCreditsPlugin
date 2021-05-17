package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class BalanceCommand implements CreditSubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender instanceof Player player) {
            final BigDecimal balance = player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance();
            sender.sendMessage("You balance is : " + helper.formatBigDecimal(balance));
        } else
            sender.sendMessage("Only Players use this Command");

        return true;
    }

    @Override
    public String getName() {
        return "balance";
    }
}
