package gg.solarmc.solarcredits.config.configs;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.List;

public interface ItemConfig {

    @ConfKey("material")
    @ConfDefault.DefaultString("diamond")
    String material();

    @ConfKey("priceincredits")
    @ConfDefault.DefaultDouble(1)
    double priceInCredits();

    @ConfKey("command")
    @ConfDefault.DefaultString("give @p minecraft:diamond")
    String command();

    @ConfKey("message")
    @ConfDefault.DefaultString("You have got a Diamond :D")
    String message();

    @ConfKey("displayName")
    @ConfDefault.DefaultString("Diemond")
    String displayName();

    @ConfKey("lore")
    @ConfDefault.DefaultStrings({"This is a diamond if you didn't know :D"})
    List<String> lore();
}
