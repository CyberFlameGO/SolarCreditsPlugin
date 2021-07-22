package gg.solarmc.solarcredits.config;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.Map;

public interface RotatingShopConfig {

    static Map<String, @SubSection ItemConfig> setDefaultItem(@SubSection ItemConfig defaultConfig) {
        return Map.of("Item", defaultConfig);
    }

    @ConfKey("tebex.secret")
    @ConfDefault.DefaultString("")
    String tebexSecret();

    @ConfKey("items")
    @ConfDefault.DefaultObject("setDefaultItem")
    Map<String, @SubSection ItemConfig> items();

}
