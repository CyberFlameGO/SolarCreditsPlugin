package gg.solarmc.solarcredits;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreditCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			String subCommand = args[0];
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (subCommand.equalsIgnoreCase("shop")) {

				} else if (subCommand.equalsIgnoreCase("spend")) {

				} else if (subCommand.equalsIgnoreCase("balance")) {

				}
			}
		} else if (args.length == 3) {
			String subCommand = args[0];
			String playerName = args[1];
			String amount = args[2];
			if (isNumeric(amount)) {
				Player receiver = Bukkit.getPlayerExact(playerName);
				if (receiver == null) {
					sender.sendMessage("Sorry, but i'm not able to find the player " + playerName + " !");
					return true;
				}

				if (subCommand.equalsIgnoreCase("send")) {

				} else if (subCommand.equalsIgnoreCase("add")) {

				} else if (subCommand.equalsIgnoreCase("remove")) {

				} else if (subCommand.equalsIgnoreCase("set")) {

				}
			} else {
				sender.sendMessage("Sorry, but " + amount + " is not a double!");
			}
		}
		return true;
	}

	public boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);

		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
