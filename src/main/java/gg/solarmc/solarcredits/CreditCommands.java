package gg.solarmc.solarcredits;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.menus.ConfirmMenu;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

public class CreditCommands implements CommandExecutor {

    private final SolarCredit plugin;
    private static final Logger logger = LoggerFactory.getLogger(CreditCommands.class);
    private final JSONParser parser = new JSONParser();

    private final String X_TEBEX_SECRET;

    public CreditCommands(SolarCredit plugin, @NotNull String TebexSecret) {
        this.plugin = plugin;
        this.X_TEBEX_SECRET = TebexSecret;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        String subCommand = args[0];
        if (args.length == 1) {
            if (sender instanceof Player player) {
                if (subCommand.equalsIgnoreCase("reload")) { // credits reload
                    if (player.hasPermission("credits.reload"))
                        plugin.reloadConfig();
                    else
                        player.sendMessage("You don't have permission to reload this plugin");
                } else if (subCommand.equalsIgnoreCase("shop")) { // credits shop
                    plugin.getShop().openShop(player);
                } else if (subCommand.equalsIgnoreCase("balance")) { // credits balance
                    sender.sendMessage("Your balance is : "
                            + player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance().floatValue());
                }
            }
        } else if (args.length == 2) {
            if (subCommand.equalsIgnoreCase("spend")) { // credits spend
                String amountString = args[1];

                if (isNumberCorrectly(amountString)) {
                    double amount = Double.parseDouble(amountString);
                    if (sender instanceof Player player) {
                        ItemStack giftCard = new ItemStack(Material.MAP);
                        final ItemMeta giftMeta = giftCard.getItemMeta();
                        giftMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Gift Card");
                        giftMeta.setLore(List.of(ChatColor.AQUA + "$" + amount));
                        giftCard.setItemMeta(giftMeta);

                        ConfirmMenu confirmMenu = new ConfirmMenu("Confirm Spend", giftCard, null,
                                confirmed -> {
                                    if (confirmed) {
                                        plugin.getServer().getDataCenter()
                                                .runTransact(transaction -> {
                                                    WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE)
                                                            .withdrawBalance(transaction, BigDecimal.valueOf(amount));

                                                    if (result.isSuccessful()) {
                                                        createGiftCard(player, amount, giftCardCode -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aGift Card Code : &r&l&6" + giftCardCode)));
                                                    } else {
                                                        player.sendMessage("Sorry, you don't have enough money!");
                                                    }
                                                })
                                                .exceptionally((ex) -> {
                                                    logger.error("Failed to withdraw {} from {}", amount, player, ex);
                                                    return null;
                                                });
                                    }
                                }
                        );

                        confirmMenu.open(player);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use this command!!");
                    }
                }
            }
        } else if (args.length == 3) {
            String playerName = args[1];
            String amountString = args[2];

            if (isNumberCorrectly(amountString)) {
                double amount = Double.parseDouble(amountString);
                Player receiver = plugin.getServer().getPlayerExact(playerName);

                if (receiver == null) {
                    sender.sendMessage("Sorry, but i'm not able to find the player " + playerName + " !");
                    return true;
                }

                if (subCommand.equalsIgnoreCase("send") && sender instanceof Player player) { // credits send <> <>
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
                            .runTransact(transaction -> receiver.getSolarPlayer().getData(CreditsKey.INSTANCE)
                                    .depositBalance(transaction, BigDecimal.valueOf(amount)))
                            .thenRunSync(() -> sender.sendMessage("Done!"))
                            .exceptionally((ex) -> {
                                logger.error("Failed to add {} into account of {}", amount, receiver, ex);
                                return null;
                            });
                } else if (subCommand.equalsIgnoreCase("remove")) { // credits remove <> <>
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> {
                                WithdrawResult result = receiver.getSolarPlayer().getData(CreditsKey.INSTANCE)
                                        .withdrawBalance(transaction, BigDecimal.valueOf(amount));
                                if (result.isSuccessful()) {
                                    sender.sendMessage("Done without errors!");
                                } else {
                                    sender.sendMessage("Sorry, he doesn't have enough money!");
                                }
                            })
                            .thenRunSync(() -> sender.sendMessage("Done!"))
                            .exceptionally((ex) -> {
                                logger.error("Failed to remove {} into account of {}", amount, receiver, ex);
                                return null;
                            });
                } else if (subCommand.equalsIgnoreCase("set")) { // credits set <> <>
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> {
                                receiver.getSolarPlayer().getData(CreditsKey.INSTANCE).setBalance(transaction, BigDecimal.valueOf(amount));
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

    /**
     * expiresAt yyyy-mm-dd hh:mm:ss
     * amount Currency Value of the gift card
     */
    public void createGiftCard(CommandSender sender, double amount, /*String expiresAt,*/ Consumer<String> giftCardCode) {
        try {
            RequestBody body = new FormBody.Builder()
                    .add("amount", String.valueOf(amount))
                    // .add("expires_at", expiresAt)
                    .build();
            String GIFT_CARDS = "https://plugin.tebex.io/gift-cards";
            Request request = new Request.Builder()
                    .url(GIFT_CARDS)
                    .addHeader("X-Tebex-Secret", X_TEBEX_SECRET)
                    .post(body)
                    .build();

            try (Response response = plugin.getOkHttpClient().newCall(request).execute()) {
                if (response.code() == 403) {
                    Player player = (Player) sender;
                    plugin.getServer().getDataCenter()
                            .runTransact(transaction -> (player).getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(amount)))
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
        } catch (IOException | ParseException ex) {
            sender.sendMessage(ChatColor.RED + "Something went wrong, please try again later");
            logger.error("Could not create the giftcard ", ex);
        }
    }

    private String parseJsonAndGetCode(String json) throws ParseException {
        final JSONObject data = (JSONObject) ((JSONObject) parser.parse(json)).get("data");
        return (String) data.get("code");
    }

    public boolean isNumberCorrectly(String strNum) {
        if (strNum == null)
            return false;

        try {
            double d = Double.parseDouble(strNum);
            if (d < 0)
                return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
