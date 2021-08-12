package gg.solarmc.solarcredits.config.configs;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.List;

public interface ItemConfig {

    @ConfKey("material")
    @ConfDefault.DefaultString("")
    String material();

    @ConfKey("priceincredits")
    @ConfDefault.DefaultDouble(-1)
    double priceInCredits();

    @ConfKey("command")
    @ConfDefault.DefaultStrings("")
    List<String> command();

    @ConfKey("message")
    @ConfDefault.DefaultString("")
    String message();

    @ConfKey("displayName")
    @ConfDefault.DefaultString("")
    String displayName();

    @ConfKey("lore")
    @ConfDefault.DefaultStrings({""})
    List<String> lore();
}
