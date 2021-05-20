package gg.solarmc.solarcredits.command.commands;

import com.google.gson.JsonParser;
import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.command.CreditSubCommand;
import gg.solarmc.solarcredits.menus.ConfirmMenu;
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

public record SpendCommand(SolarCredit plugin, String tebexSecret) implements CreditSubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandHelper helper) {
        if (sender instanceof Player player) {
            if (args.length >= 1) {
                final String amountString = args[0];
                double amount = helper.getValidNumber(amountString);
                final BigDecimal amountInDecimal = BigDecimal.valueOf(amount);

                if (amount != -1) {
                    ItemStack giftCard = new ItemStack(Material.MAP);
                    ItemMeta giftMeta = giftCard.getItemMeta();
                    giftMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Gift Card");
                    giftMeta.setLore(List.of(ChatColor.AQUA + "$" + helper.formatBigDecimal(amountInDecimal)));
                    giftCard.setItemMeta(giftMeta);

                    final Logger logger = helper.getLogger();

                    final ConfirmMenu confirmMenu = new ConfirmMenu.Builder(giftCard)
                            .title(giftCard.getItemMeta().getDisplayName())
                            .setOnConfirm(c -> {
                                if (c) {
                                    plugin.getServer().getDataCenter()
                                            .runTransact(transaction -> {
                                                WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE)
                                                        .withdrawBalance(transaction, amountInDecimal);

                                                if (result.isSuccessful())
                                                    createGiftCard(player, amount,
                                                            giftCardCode ->
                                                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aGift Card Code : &r&l&6" + giftCardCode)),
                                                            logger);
                                                else
                                                    player.sendMessage(ChatColor.RED + "Sorry, you don't have enough money!");
                                            })
                                            .exceptionally((ex) -> {
                                                logger.error("Failed to withdraw {} from {}", amount, player, ex);
                                                return null;
                                            });
                                }
                            })
                            .build();
                    confirmMenu.open(player);
                } else
                    sender.sendMessage(ChatColor.RED + "Sorry, but " + amountString + " is not a number!");
            } else
                sender.sendMessage(ChatColor.RED + "Please specify the Amount you want to use for the Gift Card");
        } else
            sender.sendMessage(ChatColor.RED + "Only Players use this Command");

        return true;
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
                if (response.code() == 403) {
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> sender.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(amount)))
                            .thenRunSync(() -> sender.sendMessage(ChatColor.RED + "Something went wrong, Please try again later!"))
                            .exceptionally(e -> {
                                logger.error("Failed to add {} into account of {}", amount, sender.getName(), e);
                                return null;
                            });
                    return;
                }

                String code = parseJsonAndGetCode(response.body().string());
                logger.info("A gift-card was created by {} : ${}", sender.getName(), amount);
                giftCardCode.accept(code);
            }
        } catch (IOException ex) {
            sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later");
            throw new UncheckedIOException(ex);
        }
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
}
