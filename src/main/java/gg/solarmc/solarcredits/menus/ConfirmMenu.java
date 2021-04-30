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

    public ConfirmMenu(String title, ItemStack item, Menu menuBefore, Consumer<Boolean> confirmed) {
        this.confirmMenu = ChestMenu.builder(3)
                .title(title)
                .build();

        confirmMenu.getSlot(13).setItem(item);

        ItemStack deny = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        final ItemMeta denyMeta = deny.getItemMeta();
        denyMeta.setDisplayName(ChatColor.RED + "Deny");
        deny.setItemMeta(denyMeta);

        ItemStack confirm = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        final ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm");
        confirm.setItemMeta(confirmMeta);

        for (int i = 0; i < confirmMenu.getDimensions().getArea(); i++) {
            final Slot slot = confirmMenu.getSlot(i);

            switch (i % 9) {
                case 0, 1, 2 -> {
                    slot.setItem(deny);
                    slot.setClickHandler((p, info) -> {
                        p.closeInventory();
                        confirmed.accept(false);
                        if (menuBefore != null)
                            menuBefore.open(p);
                    });
                }
                case 6, 7, 8 -> {
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

