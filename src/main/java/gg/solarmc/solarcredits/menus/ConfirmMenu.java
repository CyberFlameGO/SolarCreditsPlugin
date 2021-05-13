package gg.solarmc.solarcredits.menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.mask.RecipeMask;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.function.Consumer;

public class ConfirmMenu {
    private final Menu confirmMenu;

    public ConfirmMenu(String title, ItemStack item, Menu menuBefore, Consumer<Boolean> confirmed) {
        this.confirmMenu = ChestMenu.builder(3)
                .title(title)
                .build();

        for (int i = 0; i < confirmMenu.getDimensions().getArea(); i++) {
            final Slot slot = confirmMenu.getSlot(i);

            switch (i % 9) {
                case 0, 1, 2 -> slot.setClickHandler((p, info) -> {
                    p.closeInventory();
                    confirmed.accept(false);
                    if (menuBefore != null)
                        menuBefore.open(p);
                });
                case 6, 7, 8 -> slot.setClickHandler((p, info) -> {
                    confirmed.accept(true);
                    p.closeInventory();
                });
            }
        }
    }

    public void open(Player player) {
        confirmMenu.open(player);
    }

    static class Builder {
        private final ChestMenu.Builder builder;
        private final ItemStack item;

        public Builder(ItemStack item) {
            this.builder = ChestMenu.builder(3);
            this.item = item;
        }

        public Builder title(String title) {
            builder.title(title);
            return this;
        }

        public Menu build() {
            Menu confirmMenu = builder.build();

            ItemStack deny = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            final ItemMeta denyMeta = deny.getItemMeta();
            denyMeta.setDisplayName(ChatColor.RED + "Deny");
            deny.setItemMeta(denyMeta);

            ItemStack confirm = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            final ItemMeta confirmMeta = confirm.getItemMeta();
            confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm");
            confirm.setItemMeta(confirmMeta);

            Mask mask = RecipeMask.builder(confirmMenu)
                    .item('d', deny)
                    .item('c', confirm)
                    .item('i', item)
                    .pattern("ddd000ccc")
                    .pattern("ddd0i0ccc")
                    .pattern("ddd000ccc")
                    .build();
            mask.apply(confirmMenu);

            return confirmMenu;
        }
    }
}

