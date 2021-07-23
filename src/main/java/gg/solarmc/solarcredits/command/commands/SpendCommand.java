package gg.solarmc.solarcredits.command.commands;

import com.google.gson.JsonParser;
import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import gg.solarmc.solarcredits.config.configs.CommandMessageConfig;
import gg.solarmc.solarcredits.menus.ConfirmMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

public record SpendCommand(SolarCredit plugin, String tebexSecret,
                           CommandMessageConfig config) implements CreditSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (tebexSecret == null || tebexSecret.isEmpty()) {
            sender.sendMessage("Please report this to a Admin, Tebex Secret is not Specified");
            helper.getLogger().warn("Please specify your Tebex Secret in the config!!");
            return;
        }

        if (sender instanceof Player player) {
            if (args.length >= 1) {
                final String amountString = args[0];
                double amount = helper.getValidNumber(amountString);
                final BigDecimal amountInDecimal = BigDecimal.valueOf(amount);

                if (amount != -1) {
                    ItemStack giftCard = new ItemStack(Material.PAPER);
                    ItemMeta giftMeta = giftCard.getItemMeta();
                    giftMeta.displayName(Component.text("Gift Card", NamedTextColor.GOLD, TextDecoration.BOLD));
                    giftMeta.lore(List.of(Component.text("$" + helper.formatBigDecimal(amountInDecimal), NamedTextColor.AQUA)));
                    giftCard.setItemMeta(giftMeta);

                    Logger logger = helper.getLogger();

                    final ConfirmMenu confirmMenu = new ConfirmMenu.Builder(giftCard)
                            .title(helper.stripColorCode(giftCard.getItemMeta().displayName()))
                            .setOnConfirm(c -> {
                                if (c) {
                                    plugin.getServer().getDataCenter()
                                            .runTransact(transaction -> {
                                                WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE)
                                                        .withdrawBalance(transaction, amountInDecimal);

                                                if (result.isSuccessful())
                                                    createGiftCard(player, amount,
                                                            giftCardCode -> player.sendMessage(new String[]{
                                                                    helper.translateToColor(config.successful()),
                                                                    helper.translateToColor("&aGift Card Code : &l&6" + giftCardCode)
                                                            }),
                                                            logger);
                                                else
                                                    player.sendMessage(ChatColor.RED + "Sorry, you don't have enough money!");
                                            })
                                            .exceptionally((ex) -> {
                                                sendError(sender);
                                                logger.error("Failed to withdraw {} from {}", amount, player, ex);
                                                return null;
                                            });
                                }
                            }).build();
                    confirmMenu.open(player);
                } else
                    sender.sendMessage(ChatColor.RED + "Sorry, but " + amountString + " is not a number!");
            } else
                sender.sendMessage(ChatColor.RED + "Please specify the Amount you want to use for the Gift Card");
        } else
            sender.sendMessage(ChatColor.RED + "Only Players use this Command");
    }

    /**
     * amount Currency Value of the gift card
     */
    private void createGiftCard(Player sender, double amount, Consumer<String> giftCardCode, Logger logger) {
        try {
            RequestBody body = new FormBody.Builder()
                    .add("amount", String.valueOf(amount))
                    .build();
            String GIFT_CARDS = "https://plugin.tebex.io/gift-cards";
            Request request = new Request.Builder()
                    .url(GIFT_CARDS)
                    .addHeader("X-Tebex-Secret", tebexSecret)
                    .post(body)
                    .build();

            try (Response response = plugin.getOkHttpClient().newCall(request).execute()) {
                if (response.code() != 200) {
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> sender.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(amount)))
                            .thenRunSync(() -> sendError(sender))
                            .exceptionally(e -> {
                                logger.error("Failed to add {} into account of {}", amount, sender.getName(), e);
                                return null;
                            });
                    sender.sendMessage(Component.text("Something went wrong creating a giftcard! Report this to a admin rn!! :)"));
                    logger.error("Failed to create a gift card (Check your tebex secret?)");
                    return;
                }

                String code = parseJsonAndGetCode(response.body().string());
                logger.info("A gift-card was created by {} : ${}", sender.getName(), amount);
                giftCardCode.accept(code);
            }
        } catch (IOException ex) {
            sendError(sender);
            throw new UncheckedIOException(ex);
        }
    }

    private void sendError(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.error()));
    }

    private String parseJsonAndGetCode(String json) {
        return JsonParser.parseString(json)
                .getAsJsonObject().get("data")
                .getAsJsonObject().get("code")
                .getAsString();
    }

    @Override
    public String getName() {
        return "spend";
    }

    @Override
    public String getArgs() {
        return "[Amount]";
    }

    @Override
    public String getDescription() {
        return "Spend credits for a gift card :D";
    }
}
