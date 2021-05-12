package gg.solarmc.solarcredits.config;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.List;

public interface ItemConfig {

    @ConfKey("material")
    @ConfDefault.DefaultString("diamond_sword")
    String material();

    @ConfKey("priceincredits")
    @ConfDefault.DefaultDouble(5)
    double priceInCredits();

    @ConfKey("command")
    @ConfDefault.DefaultString("give @p diamond_sword")
    String command();

    @ConfKey("message")
    @ConfDefault.DefaultString("You were given a Diamond Sword")
    String message();

    @ConfKey("displayName")
    @ConfDefault.DefaultString("&l&cA Normal Sword")
    String displayName();

    @ConfKey("lore")
    @ConfDefault.DefaultStrings({"&aYes", "Use this sword to kill players"})
    List<String> lore();
}
