package gg.solarmc.solarcredits;

import java.math.BigDecimal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gg.solarmc.loader.DataCenter.TransactionRunner;
import gg.solarmc.loader.Transaction;
import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;

public class CreditCommands implements CommandExecutor {

	private SolarCredit plugin;
	private static final Logger logger = LoggerFactory.getLogger(CreditCommands.class);

	public CreditCommands(SolarCredit plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			String subCommand = args[0];
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (subCommand.equalsIgnoreCase("shop")) {

				} else if (subCommand.equalsIgnoreCase("spend")) {

				} else if (subCommand.equalsIgnoreCase("balance")) {
					sender.sendMessage("Your balance is : "
							+ player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance().floatValue());
				}
			}
		} else if (args.length == 3) {
			String subCommand = args[0];
			String playerName = args[1];
			String amountString = args[2];
			if (isNumeric(amountString)) {
				double amount = Double.parseDouble(amountString);
				Player receiver = plugin.getServer().getPlayerExact(playerName);
				if (receiver == null) {
					sender.sendMessage("Sorry, but i'm not able to find the player " + playerName + " !");
					return true;
				}

				if (subCommand.equalsIgnoreCase("send") && sender instanceof Player) {
					Player player = (Player) sender;
					plugin.getServer().getDataCenter().runTransact(new TransactionRunner() {
						@Override
						public void runTransactUsing(Transaction transaction) {
							WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE)
									.withdrawBalance(transaction, BigDecimal.valueOf(amount));
							if (result.isSuccessful()) {
								receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction,
										BigDecimal.valueOf(amount));
							} else {
								player.sendMessage("Sorry, you don't have enough money!");
							}
						}
					}).thenRunSync(new Runnable() {
						@Override
						public void run() {
							player.sendMessage("Done!");
						}
					}).exceptionally((ex) -> {
						logger.error("Failed to deposit {} into account of {} from {}", amount, receiver, player, ex);
						return null;
					});
				} else if (subCommand.equalsIgnoreCase("add")) {
					plugin.getServer().getDataCenter().runTransact(new TransactionRunner() {
						@Override
						public void runTransactUsing(Transaction transaction) {
							receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction,
									BigDecimal.valueOf(amount));
						}
					}).thenRunSync(new Runnable() {
						@Override
						public void run() {
							sender.sendMessage("Done!");
						}
					}).exceptionally((ex) -> {
						logger.error("Failed to add {} into account of {}", amount, receiver, ex);
						return null;
					});
				} else if (subCommand.equalsIgnoreCase("remove")) {
					plugin.getServer().getDataCenter().runTransact(new TransactionRunner() {
						@Override
						public void runTransactUsing(Transaction transaction) {
							receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).withdrawBalance(transaction,
									BigDecimal.valueOf(amount));
						}
					}).thenRunSync(new Runnable() {
						@Override
						public void run() {
							sender.sendMessage("Done!");
						}
					}).exceptionally((ex) -> {
						logger.error("Failed to remove {} into account of {}", amount, receiver, ex);
						return null;
					});
				} else if (subCommand.equalsIgnoreCase("set")) {
					plugin.getServer().getDataCenter().runTransact(new TransactionRunner() {
						@Override
						public void runTransactUsing(Transaction transaction) {
							// TODO Set Balance
							// receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).balan
						}
					}).thenRunSync(new Runnable() {
						@Override
						public void run() {
							sender.sendMessage("Done!");
						}
					}).exceptionally((ex) -> {
						logger.error("Failed to set {} into account of {}", amount, receiver, ex);
						return null;
					});
				}
			} else {
				sender.sendMessage("Sorry, but " + amountString + " is not a double!");
			}
		}
		return true;
	}

	public boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
