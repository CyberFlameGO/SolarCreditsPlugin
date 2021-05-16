package gg.solarmc.solarcredits.menus;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.RotatingItem;
import gg.solarmc.solarcredits.SolarCredit;
import gg.solarmc.solarcredits.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * itemName:
 * material:
 * priceincredits:
 * command:
 * message:
 * displayName: OPTIONAL
 * lore: OPTIONAL
 */
public class RotatingShopMenu {
    private final Logger LOGGER = LoggerFactory.getLogger(RotatingShopMenu.class);
    private final SolarCredit plugin;
    private final Menu creditsShop;
    private long lastDay;

    private final List<RotatingItem> rotatingItems;
    private final List<Set<UUID>> playersInteracted = new ArrayList<>();

    public RotatingShopMenu(SolarCredit plugin, Config config) {
        for (int i = 0; i < 4; i++) {
            playersInteracted.add(new HashSet<>());
        }
        this.plugin = plugin;
        creditsShop = ChestMenu.builder(3)
                .title("Credits Shop")
                .build();

        rotatingItems = config.getRotatingItems();
    }

    public void openShop(Player player) {
        RotatingItem[] items = getItems();

        creditsShop.getSlot(10)//.setItem(balance);
                .setItemTemplate(p -> {
                    BigDecimal credits = p.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance();
                    ItemStack balance = new ItemStack(Material.MAP);
                    ItemMeta balMeta = balance.getItemMeta();
                    balMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Balance : " + credits);
                    balance.setItemMeta(balMeta);
                    return balance;
                });


        for (int i = 0; i < items.length; i++) {
            final RotatingItem rotatingItem = items[i];
            ItemStack item = new ItemStack(rotatingItem.material());

            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.setDisplayName(rotatingItem.displayName() == null ? rotatingItem.name() : ChatColor.translateAlternateColorCodes('&', rotatingItem.displayName()));

            final Slot slot = creditsShop.getSlot(i + 12);

            if (playersInteracted.get(i).contains(player.getUniqueId())) {
                itemMeta.setLore(List.of(ChatColor.GOLD + "Bought"));
                item.setItemMeta(itemMeta);
                slot.setItem(item);
                continue;
            }

            itemMeta.setLore(List.of(ChatColor.AQUA + "Price: " + rotatingItem.priceInCredits() + " credits"));

            if (rotatingItem.lore() != null) {
                final List<String> lore = itemMeta.getLore();
                lore.addAll(rotatingItem.lore());
                itemMeta.setLore(lore.stream().map(it -> ChatColor.translateAlternateColorCodes('&', it)).collect(Collectors.toList()));
            }

            item.setItemMeta(itemMeta);
            slot.setItem(item);

            int slotId = i;
            slot.setClickHandler((p, info) -> {
                if (playersInteracted.get(slotId).contains(p.getUniqueId())) {
                    p.sendMessage(ChatColor.RED + "You can not buy this Item Again!!");
                    return;
                }
                openConfirmMenu(p, slotId, item, rotatingItem);
            });
        }

        creditsShop.open(player);
    }

    public void openConfirmMenu(Player player, int slotId, ItemStack item, RotatingItem rotatingItem) {
        // player.closeInventory();
        final Consumer<Boolean> confirmed = (c) -> {
            if (c) {
                plugin.getServer().getDataCenter()
                        .runTransact((transaction) -> {
                            final WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE).withdrawBalance(transaction, BigDecimal.valueOf(rotatingItem.priceInCredits()));
                            if (result.isSuccessful()) {
                                // Bukkit.dispatchCommand(console, rotatingItem.getCommand());
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), rotatingItem.command());
                            } else {
                                player.sendMessage("Sorry, you don't have enough money!");
                            }
                        })
                        .thenRunSync(() -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', rotatingItem.message())))
                        .exceptionally((ex) -> {
                            LOGGER.error("Exception in Credits Shop Transaction", ex);
                            return null;
                        });

                final Set<UUID> uuids = playersInteracted.get(slotId);
                uuids.add(player.getUniqueId());
                playersInteracted.set(slotId, uuids);
            }
        };

        final ConfirmMenu confirmMenu = new ConfirmMenu.Builder(item)
                .title(item.getItemMeta().getDisplayName())
                .setMenuBefore(creditsShop)
                .setOnConfirm(confirmed)
                .build();

        confirmMenu.open(player);
    }

    public RotatingItem[] getItems() {
        final long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        if (lastDay - days == 1) {
            playersInteracted.clear();
            lastDay = days;
        }
        final int group = ((int) days % (rotatingItems.size() / 4)) * 4;
        return new RotatingItem[]{rotatingItems.get(group), rotatingItems.get(group + 1), rotatingItems.get(group + 2), rotatingItems.get(group + 3)};
    }
}

