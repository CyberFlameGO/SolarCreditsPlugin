package gg.solarmc.solarcredits.command;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.SolarCredit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.arim.omnibus.util.ThisClass;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;

public record CommandHelper(SolarCredit plugin) {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThisClass.get());
    private static final DecimalFormat df = new DecimalFormat("#,###.00");

    public Logger getLogger() {
        return LOGGER;
    }

    public String formatBigDecimal(BigDecimal number) {
        return df.format(number);
    }

    public double getValidNumber(String strNum) {
        if (strNum == null)
            return -1;

        try {
            double d = Double.parseDouble(strNum);
            if (d < 0)
                return -1;
            return d;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Only for transaction commands like send, add, remove and set
     */
    public void validateAndRun(CommandSender sender, String[] args, BiConsumer<Player, Double> transaction) {
        if (args.length >= 3) {
            String playerName = args[0];
            double amount = getValidNumber(args[1]);

            if (amount != -1) {
                Player receiver = plugin.getServer().getPlayerExact(playerName);

                if (receiver == null)
                    sender.sendMessage(ChatColor.RED + "Sorry, but i'm not able to find the player " + playerName + " !");

                transaction.accept(receiver, amount);
            } else {
                sender.sendMessage(ChatColor.RED + "Sorry, but " + amount + " is not a number!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify the Player and amount both!");
        }
    }

    public void sendCredits(Player sender, Player receiver, double amount) {
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> {
                    WithdrawResult result = sender.getSolarPlayer().getData(CreditsKey.INSTANCE)
                            .withdrawBalance(transaction, BigDecimal.valueOf(amount));
                    if (result.isSuccessful()) {
                        receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction,
                                BigDecimal.valueOf(amount));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Sorry, you don't have enough money!");
                    }
                })
                .thenRunSync(() -> sender.sendMessage(ChatColor.GREEN + "Done!"))
                .exceptionally((ex) -> {
                    sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later...");
                    LOGGER.error("Failed to deposit {} into account of {} from {}", amount, receiver, sender, ex);
                    return null;
                });
    }

    public void addCredits(CommandSender sender, Player receiver, double amount) {
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> receiver.getSolarPlayer().getData(CreditsKey.INSTANCE)
                        .depositBalance(transaction, BigDecimal.valueOf(amount)))
                .thenRunSync(() -> sender.sendMessage(ChatColor.GREEN + "Done!"))
                .exceptionally((ex) -> {
                    sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later...");
                    LOGGER.error("Failed to add {} into account of {}", amount, receiver, ex);
                    return null;
                });
    }

    public void removeCredits(CommandSender sender, Player receiver, double amount) {
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> {
                    WithdrawResult result = receiver.getSolarPlayer().getData(CreditsKey.INSTANCE)
                            .withdrawBalance(transaction, BigDecimal.valueOf(amount));
                    if (result.isSuccessful()) {
                        sender.sendMessage(ChatColor.GREEN + "Done!");
                    } else {
                        sender.sendMessage("Sorry, he doesn't have enough money!");
                    }
                })
                .thenRunSync(() -> sender.sendMessage("Done!"))
                .exceptionally((ex) -> {
                    sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later...");
                    LOGGER.error("Failed to remove {} into account of {}", amount, receiver, ex);
                    return null;
                });
    }

    public void setCredits(CommandSender sender, Player receiver, double amount) {
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).setBalance(transaction, BigDecimal.valueOf(amount)))
                .thenRunSync(() -> sender.sendMessage(ChatColor.GREEN + "Done!"))
                .exceptionally((ex) -> {
                    sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later...");
                    LOGGER.error("Failed to set {} into account of {}", amount, receiver, ex);
                    return null;
                });
    }

}
