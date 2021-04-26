package gg.solarmc.solarcredits.menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.function.Consumer;

public class ConfirmMenu {
    private final Menu confirmMenu;

    public ConfirmMenu(Menu menu, ItemStack item, Consumer<Boolean> confirmed) {
        this.confirmMenu = ChestMenu.builder(3)
                .title(item.getItemMeta().getDisplayName())
                .build();

        confirmMenu.getSlot(13).setItem(item);

        for (int i = 0; i < confirmMenu.getDimensions().getArea(); i++) {
            final int row = i % 9;
            final Slot slot = confirmMenu.getSlot(i);

            switch (row) {
                case 0, 1, 2 -> {
                    ItemStack deny = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
                    final ItemMeta meta = deny.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "Deny");
                    deny.setItemMeta(meta);

                    slot.setItem(deny);
                    slot.setClickHandler((p, info) -> {
                        p.closeInventory();
                        confirmed.accept(false);
                        menu.open(p);
                    });
                }
                case 6, 7, 8 -> {
                    ItemStack confirm = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
                    final ItemMeta meta = confirm.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + "Confirm");
                    confirm.setItemMeta(meta);

                    slot.setItem(confirm);
                    slot.setClickHandler((p, info) -> {
                        confirmed.accept(true);
                        p.closeInventory();
                    });
                }
            }
        }
    }

    public void open(Player player) {
        confirmMenu.open(player);
    }
}

