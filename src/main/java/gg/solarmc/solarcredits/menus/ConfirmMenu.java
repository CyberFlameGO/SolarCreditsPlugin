package gg.solarmc.solarcredits.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.mask.SolarMask;
import org.ipvp.canvas.type.ChestMenu;

import java.util.function.Consumer;

public class ConfirmMenu {
    private final Menu confirmMenu;

    private ConfirmMenu(Menu menu) {
        this.confirmMenu = menu;
    }

    public void open(Player player) {
        confirmMenu.open(player);
    }

    public static class Builder {
        private final ChestMenu.Builder builder;
        private final ItemStack item;
        private Consumer<Boolean> confirmed;
        private Menu menuBefore;

        public Builder(ItemStack item) {
            this.builder = ChestMenu.builder(3);
            this.item = item;
        }

        public Builder title(String title) {
            builder.title(title);
            return this;
        }

        public Builder setMenuBefore(Menu menu) {
            this.menuBefore = menu;
            return this;
        }

        public Builder setOnConfirm(Consumer<Boolean> confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public ConfirmMenu build() {
            Menu confirmMenu = builder.build();

            ItemStack deny = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            final ItemMeta denyMeta = deny.getItemMeta();
            denyMeta.displayName(Component.text("Deny", NamedTextColor.RED));
            deny.setItemMeta(denyMeta);

            ItemStack confirm = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            final ItemMeta confirmMeta = confirm.getItemMeta();
            confirmMeta.displayName(Component.text("Confirm", NamedTextColor.GREEN));
            confirm.setItemMeta(confirmMeta);

            Mask mask = SolarMask.builder(confirmMenu)
                    .item('d', deny,
                            (p, info) -> {
                                p.closeInventory();
                                confirmed.accept(false);
                                if (menuBefore != null)
                                    menuBefore.open(p);
                            })
                    .item('c', confirm
                            , (p, info) -> {
                                confirmed.accept(true);
                                p.closeInventory();
                            }
                    )
                    .item('i', item)
                    .pattern("ddd000ccc")
                    .pattern("ddd0i0ccc")
                    .pattern("ddd000ccc")
                    .build();
            mask.apply(confirmMenu);

            return new ConfirmMenu(confirmMenu);
        }
    }
}

