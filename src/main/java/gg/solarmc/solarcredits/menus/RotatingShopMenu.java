package gg.solarmc.solarcredits.menus;

import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.WithdrawResult;
import gg.solarmc.solarcredits.RotatingItem;
import gg.solarmc.solarcredits.SolarCredit;
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
import java.util.stream.Collectors;

/**
 * itemName:
 *  material:
 *      priceincredits:
 *      command:
 *      message:
 *      displayName: OPTIONAL
 *      lore: OPTIONAL
 */
public class RotatingShopMenu {
    private final Logger LOGGER = LoggerFactory.getLogger(RotatingShopMenu.class);
    private final SolarCredit plugin;
    private final Menu creditsShop;
    private final List<RotatingItem> rotatingItems = new ArrayList<>();
    private final List<Set<UUID>> playersInteracted = new ArrayList<>();

    private long lastDay;

    public RotatingShopMenu(SolarCredit plugin) {
        for (int i = 0; i < 4; i++) {
            playersInteracted.add(new HashSet<>());
        }
        this.plugin = plugin;
        creditsShop = ChestMenu.builder(3)
                .title("Credits Shop")
                .build();
        loadItems();
    }

    public void openShop(Player player) {
        RotatingItem[] items = getItems();

        final BigDecimal credits = player.getSolarPlayer().getData(CreditsKey.INSTANCE).currentBalance();
        final ItemStack balance = new ItemStack(Material.MAP);
        final ItemMeta balMeta = balance.getItemMeta();
        balMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Balance : " + credits);
        balance.setItemMeta(balMeta);

        creditsShop.getSlot(10).setItem(balance);

        for (int i = 0; i < items.length; i++) {
            final RotatingItem rotatingItem = items[i];
            ItemStack item = new ItemStack(rotatingItem.getMaterial());

            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.setDisplayName(rotatingItem.getDisplayName() == null ? rotatingItem.getName() : ChatColor.translateAlternateColorCodes('&', rotatingItem.getDisplayName()));

            final Slot slot = creditsShop.getSlot(i + 12);

            if (playersInteracted.get(i).contains(player.getUniqueId())) {
                itemMeta.setLore(List.of(ChatColor.GOLD + "Bought"));
                item.setItemMeta(itemMeta);
                slot.setItem(item);
                continue;
            }

            itemMeta.setLore(List.of(ChatColor.AQUA + "Price: " + rotatingItem.getPriceInCredits() + " credits"));

            if (rotatingItem.getLore() != null) {
                final List<String> lore = itemMeta.getLore();
                lore.addAll(rotatingItem.getLore());
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
        final ConfirmMenu confirmMenu = new ConfirmMenu(item.getItemMeta().getDisplayName(), item, creditsShop,
                (confirmed) -> {
                    if (confirmed) {
                        plugin.getServer().getDataCenter()
                                .runTransact((transaction) -> {
                                    final WithdrawResult result = player.getSolarPlayer().getData(CreditsKey.INSTANCE).withdrawBalance(transaction, BigDecimal.valueOf(rotatingItem.getPriceInCredits()));
                                    if (result.isSuccessful()) {
                                        // Bukkit.dispatchCommand(console, rotatingItem.getCommand());
                                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), rotatingItem.getCommand());
                                    } else {
                                        player.sendMessage("Sorry, you don't have enough money!");
                                    }
                                })
                                .thenRunSync(() -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', rotatingItem.getMessage())))
                                .exceptionally((ex) -> {
                                    LOGGER.error("Exception in Credits Shop Transaction", ex);
                                    return null;
                                });

                        final Set<UUID> uuids = playersInteracted.get(slotId);
                        uuids.add(player.getUniqueId());
                        playersInteracted.set(slotId, uuids);
                    }
                }
        );
        confirmMenu.open(player);
    }

    public void loadItems() {
        plugin.getConfig().getConfigurationSection("items").getKeys(false).forEach(key -> {
            try {
                String name = key;

                key = "items." + key;
                Material material = Material.matchMaterial(plugin.getConfig().getString(key + ".material"));
                double price = plugin.getConfig().getDouble(key + ".priceincredits");
                String command = plugin.getConfig().getString(key + ".command").replaceFirst("^/", "");
                String message = plugin.getConfig().getString(key + ".message");
                String displayName = null;
                List<String> lore = null;

                if (plugin.getConfig().contains(key + ".displayName")) {
                    displayName = plugin.getConfig().getString(key + ".displayName");
                }
                if (plugin.getConfig().contains(key + ".lore")) {
                    lore = plugin.getConfig().getStringList(key + ".lore");
                }

                rotatingItems.add(new RotatingItem(name, material, price, command, message, displayName, lore));
            } catch (NullPointerException e) {
                throw new NullPointerException("Missing key from " + key + " in rotatingshop.yml");
            }
        });
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

