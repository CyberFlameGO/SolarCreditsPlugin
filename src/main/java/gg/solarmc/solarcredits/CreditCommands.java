package gg.solarmc.solarcredits;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class CreditCommands implements CommandExecutor {

    private final SolarCredit plugin;
    private static final Logger logger = LoggerFactory.getLogger(CreditCommands.class);

    public CreditCommands(SolarCredit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (args.length == 1) {
            String subCommand = args[0];

            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (subCommand.equalsIgnoreCase("reload")) { // credits reload
                    if (player.hasPermission("credits.reload"))
                        plugin.reloadConfig();
                    else
                        player.sendMessage("You don't have permission to reload this plugin");
                } else if (subCommand.equalsIgnoreCase("shop")) { // credits shop
                    plugin.shop.openShop(player);
                } else if (subCommand.equalsIgnoreCase("spend")) { // credits spend

                } else if (subCommand.equalsIgnoreCase("balance")) { // credits balance
                    sender.sendMessage("Your balance is : "
                            + player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance().floatValue());
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0];
            String playerName = args[1];
            String amountString = args[2];

            if (isNumberCorrectly(amountString)) {
                double amount = Double.parseDouble(amountString);
                Player receiver = plugin.getServer().getPlayerExact(playerName);

                if (receiver == null) {
                    sender.sendMessage("Sorry, but i'm not able to find the player " + playerName + " !");
                    return true;
                }

                if (subCommand.equalsIgnoreCase("send") && sender instanceof Player) { // credits send <> <>
                    Player player = (Player) sender;
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> {
                                WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE)
                                        .withdrawBalance(transaction, BigDecimal.valueOf(amount));
                                if (result.isSuccessful()) {
                                    receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction,
                                            BigDecimal.valueOf(amount));
                                } else {
                                    player.sendMessage("Sorry, you don't have enough money!");
                                }
                            })
                            .thenRunSync(() -> player.sendMessage("Done!"))
                            .exceptionally((ex) -> {
                                logger.error("Failed to deposit {} into account of {} from {}", amount, receiver, player, ex);
                                return null;
                            });
                } else if (subCommand.equalsIgnoreCase("add")) { // credits add <> <>
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction,
                                    BigDecimal.valueOf(amount)))
                            .thenRunSync(() -> sender.sendMessage("Done!"))
                            .exceptionally((ex) -> {
                                logger.error("Failed to add {} into account of {}", amount, receiver, ex);
                                return null;
                            });
                } else if (subCommand.equalsIgnoreCase("remove")) { // credits remove <> <>
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> {
                                WithdrawResult result = receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).withdrawBalance(transaction,
                                        BigDecimal.valueOf(amount));
                                if (result.isSuccessful()) {
                                    sender.sendMessage("Done without errors!");
                                } else {
                                    sender.sendMessage("Sorry, he doesn't have enough money!");
                                }
                            }).thenRunSync(() -> sender.sendMessage("Done!"))
                            .exceptionally((ex) -> {
                                logger.error("Failed to remove {} into account of {}", amount, receiver, ex);
                                return null;
                            });
                } else if (subCommand.equalsIgnoreCase("set")) { // credits set <> <>
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> {
                                // TODO Set Balance
                                // receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).setBalance(transaction, BigDecimal.valueOf(amount));
                            })
                            .thenRunSync(() -> sender.sendMessage("Done!"))
                            .exceptionally((ex) -> {
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

    public boolean isNumberCorrectly(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
            if (d < 0) {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
