package gg.solarmc.solarcredits.command;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.config.configs.CommandMessageConfig;
import gg.solarmc.solarcredits.config.configs.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.arim.omnibus.util.ThisClass;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public record CommandHelper(SolarCredit plugin, MessageConfig config) {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThisClass.get());

    public Logger getLogger() {
        return LOGGER;
    }

    public String formatBigDecimal(BigDecimal number) {
        return String.format("%,.2f", number.doubleValue());
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
        if (args.length >= 2) {
            String playerName = args[0];
            double amount = getValidNumber(args[1]);

            if (amount == 0) {
                sender.sendMessage(ChatColor.RED + "Your amount can't be 0");
                return;
            }

            if (amount != -1) {
                Player receiver = plugin.getServer().getPlayerExact(playerName);

                if (receiver == null) {
                    sender.sendMessage(ChatColor.RED + "Sorry, but I'm not able to find the player " + playerName + " !");
                    return;
                }

                transaction.accept(receiver, amount);
            } else
                sender.sendMessage(ChatColor.RED + "Sorry, but " + amount + " is not a number!");
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify the Player and the Amount both!");
        }
    }

    public void sendCredits(Player sender, Player receiver, double amount) {
        CommandMessageConfig sendConfig = getMessageConfig("send");
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> {
                    WithdrawResult result = sender.getSolarPlayer().getData(CreditsKey.INSTANCE)
                            .withdrawBalance(transaction, BigDecimal.valueOf(amount));
                    if (result.isSuccessful()) {
                        receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction,
                                BigDecimal.valueOf(amount));
                        sender.sendMessage(translateToColor(sendConfig.successful()));
                    } else
                        sender.sendMessage(ChatColor.RED + "Sorry, you don't have enough money!");
                })
                .thenRunSync(() -> {
                })
                .exceptionally((ex) -> {
                    sender.sendMessage(translateToColor(sendConfig.error()));
                    LOGGER.error("Failed to deposit {} into account of {} from {}", amount, receiver, sender, ex);
                    return null;
                });
    }

    public void addCredits(CommandSender sender, Player receiver, double amount) {
        CommandMessageConfig addConfig = getMessageConfig("add");
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> receiver.getSolarPlayer().getData(CreditsKey.INSTANCE)
                        .depositBalance(transaction, BigDecimal.valueOf(amount)))
                .thenRunSync(() -> sender.sendMessage(translateToColor(addConfig.successful())))
                .exceptionally((ex) -> {
                    sender.sendMessage(translateToColor(addConfig.error()));
                    LOGGER.error("Failed to add {} into account of {}", amount, receiver, ex);
                    return null;
                });
    }

    public void removeCredits(CommandSender sender, Player receiver, double amount) {
        CommandMessageConfig removeConfig = getMessageConfig("remove");
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> {
                    WithdrawResult result = receiver.getSolarPlayer().getData(CreditsKey.INSTANCE)
                            .withdrawBalance(transaction, BigDecimal.valueOf(amount));
                    if (result.isSuccessful())
                        sender.sendMessage(translateToColor(removeConfig.successful()));
                    else
                        sender.sendMessage(ChatColor.RED + "Sorry, he doesn't have enough money!");
                })
                .thenRunSync(() -> sender.sendMessage("Done!"))
                .exceptionally((ex) -> {
                    sender.sendMessage(translateToColor(removeConfig.error()));
                    LOGGER.error("Failed to remove {} into account of {}", amount, receiver, ex);
                    return null;
                });
    }

    public void setCredits(CommandSender sender, Player receiver, double amount) {
        CommandMessageConfig setConfig = getMessageConfig("set");
        plugin.getServer().getDataCenter()
                .runTransact(transaction -> receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).setBalance(transaction, BigDecimal.valueOf(amount)))
                .thenRunSync(() -> sender.sendMessage(translateToColor(setConfig.successful())))
                .exceptionally((ex) -> {
                    sender.sendMessage(translateToColor(setConfig.error()));
                    LOGGER.error("Failed to set {} into account of {}", amount, receiver, ex);
                    return null;
                });
    }

    public CommandMessageConfig getMessageConfig(String command) {
        return config.commandMsgs().get(command.toLowerCase());
    }

    public String translateToColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public Component translateColorCode(String s) {
        return LegacyComponentSerializer.legacy('&').deserialize(s);
    }

    public String stripColorCode(Component c) {
        return PlainComponentSerializer.plain().serialize(c);
    }

    public boolean dispatchCommand(Server server, String command) {
        try {
            return server.getScheduler().callSyncMethod(plugin,
                    () -> server.dispatchCommand(server.getConsoleSender(), command.replaceFirst("^/", ""))
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Something went wrong Dispatching a command, Check if the command is correct " + command, e);
            return false;
        }
    }
}
