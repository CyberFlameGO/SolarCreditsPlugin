package gg.solarmc.solarcredits.menus;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.RotatingItem;
import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.command.CommandHelper;
import gg.solarmc.solarcredits.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * itemName:
 * material:
 * priceincredits:
 * command:
 * message:
 * displayName:
 * lore:
 */
public class RotatingShopMenu {
    private final Logger LOGGER = LoggerFactory.getLogger(RotatingShopMenu.class);
    private final SolarCredit plugin;
    private final CommandHelper helper;
    private final Menu creditsShop;
    private long lastDay;

    private final List<RotatingItem> rotatingItems;
    private final List<Set<UUID>> playersInteracted = new LinkedList<>();

    public RotatingShopMenu(SolarCredit plugin, Config config, CommandHelper helper) {
        for (int i = 0; i < 4; i++)
            playersInteracted.add(new HashSet<>());
        this.plugin = plugin;
        this.helper = helper;
        creditsShop = ChestMenu.builder(3)
                .title("Credits Shop")
                .build();

        rotatingItems = config.getRotatingItems();
    }

    public void openShop(Player player) {
        RotatingItem[] items = getItems();

        BigDecimal credits = player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance();
        ItemStack balance = new ItemStack(Material.PAPER);
        ItemMeta balMeta = balance.getItemMeta();
        balMeta.displayName(Component.text("Balance : " + credits, NamedTextColor.RED, TextDecoration.BOLD));
        balMeta.lore(List.of(
                Component.text("[", NamedTextColor.WHITE, TextDecoration.BOLD)
                        .append(Component.text(getTimeRemainingForRotate(), NamedTextColor.GREEN)
                                .append(Component.text("]", NamedTextColor.WHITE, TextDecoration.BOLD))))
        );
        balance.setItemMeta(balMeta);

        creditsShop.getSlot(10).setItem(balance);

        for (int i = 0; i < items.length; i++) {
            final RotatingItem rotatingItem = items[i];
            ItemStack item = new ItemStack(rotatingItem.material());

            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.displayName(helper.translateColorCode(rotatingItem.displayName()));

            final Slot slot = creditsShop.getSlot(i + 12);

            if (playersInteracted.get(i).contains(player.getUniqueId())) {
                itemMeta.lore(List.of(Component.text("Bought", NamedTextColor.GOLD, TextDecoration.ITALIC)));
                item.setItemMeta(itemMeta);
                slot.setItem(item);
                continue;
            }

            itemMeta.lore(List.of(Component.text("Price: " + rotatingItem.priceInCredits() + " credits", NamedTextColor.AQUA)));

            if (rotatingItem.lore() != null) {
                final List<Component> components = rotatingItem.lore().stream().map(helper::translateColorCode).toList();
                itemMeta.lore(components);
            }

            item.setItemMeta(itemMeta);
            slot.setItem(item);

            int slotId = i;

            slot.setClickHandler((p, info) -> {
                if (playersInteracted.get(slotId).contains(p.getUniqueId())) {
                    p.sendMessage(ChatColor.RED + "You can not buy this Item Again!!");
                    return;
                }

                if (rotatingItem.command().replace("/", "").startsWith("give") && player.getInventory().firstEmpty() == -1) {
                    p.sendMessage("Your inventory is full!");
                    return;
                }

                openConfirmMenu(p, slotId, item, rotatingItem);
            });
        }

        creditsShop.open(player);
    }

    public void openConfirmMenu(Player player, int slotId, ItemStack item, RotatingItem rotatingItem) {
        final Consumer<Boolean> confirmed = (c) -> {
            if (c) {
                Server server = plugin.getServer();
                server.getDataCenter()
                        .runTransact((transaction) -> {
                            final WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE).withdrawBalance(transaction, BigDecimal.valueOf(rotatingItem.priceInCredits()));
                            if (result.isSuccessful()) {
                                if (helper.dispatchCommand(server, rotatingItem.command().replace("@p", player.getName()))) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', rotatingItem.message()));

                                    Set<UUID> uuids = playersInteracted.get(slotId);
                                    uuids.add(player.getUniqueId());
                                    playersInteracted.set(slotId, uuids);
                                    return;
                                }

                                player.sendMessage(ChatColor.RED + "Something went wrong, please report this to Admins");
                                LOGGER.error("There was a problem dispatching a command from key " + rotatingItem.key() + " in rotatingshop.yml");
                            } else
                                player.sendMessage(ChatColor.RED + "Sorry, you don't have enough money!");
                        })
                        .exceptionally((ex) -> {
                            player.sendMessage(ChatColor.RED + "Something went wrong, please try again later...");
                            LOGGER.error("Exception in Credits Shop Transaction", ex);
                            return null;
                        });
            }
        };

        final ConfirmMenu confirmMenu = new ConfirmMenu.Builder(item)
                .title(helper.stripColorCode(item.getItemMeta().displayName()))
                .setMenuBefore(creditsShop)
                .setOnConfirm(confirmed)
                .build();

        confirmMenu.open(player);
    }

    public RotatingShopMenu setLastDay(long lastDay) {
        this.lastDay = lastDay;
        return this;
    }

    public long getLastDay() {
        return lastDay;
    }

    public RotatingShopMenu setPlayersInteracted(List<Set<UUID>> playersInteracted) {
        Collections.copy(this.playersInteracted, playersInteracted);
        return this;
    }

    public List<Set<UUID>> getPlayersInteracted() {
        return playersInteracted;
    }

    public String getTimeRemainingForRotate() {
        final long milliDay = TimeUnit.DAYS.toMillis(1);
        long timeRemaining = (milliDay - (Instant.now().toEpochMilli() % milliDay)) / 1000;

        long mins = timeRemaining / 60 % 60;
        long hours = (timeRemaining - mins) / 3600;

        return String.format("%s H %s M Remaining for Rotation", hours, mins);
    }

    public RotatingItem[] getItems() {
        final long days = TimeUnit.MILLISECONDS.toDays(Instant.now().toEpochMilli());
        if (lastDay < days) {
            Collections.fill(playersInteracted, new HashSet<>());
            lastDay = days;
        }
        final int group = ((int) days % (rotatingItems.size() / 4)) * 4;
        return new RotatingItem[]{rotatingItems.get(group), rotatingItems.get(group + 1), rotatingItems.get(group + 2), rotatingItems.get(group + 3)};
    }
}

