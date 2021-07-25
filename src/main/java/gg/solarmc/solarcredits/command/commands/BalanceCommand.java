package gg.solarmc.solarcredits.command.commands;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public record BalanceCommand(SolarCredit plugin) implements CreditSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender instanceof Player player) {
            BigDecimal balance = getBalance(player);

            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "Your balance is : " + helper.formatBigDecimal(balance));
                return;
            }

            player = plugin.getServer().getPlayerExact(args[0]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Sorry, but I'm not able to find the player " + args[0] + " ! " + ChatColor.GREEN + "Remember the player needs to be online :)");
                return;
            }

            balance = getBalance(player);
            sender.sendMessage(ChatColor.GREEN + player.getName() + "'s balance is : " + helper.formatBigDecimal(balance));
        } else
            sender.sendMessage(ChatColor.RED + "Only Players use this Command");
    }

    private BigDecimal getBalance(Player player) {
        return player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance();
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
