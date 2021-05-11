package gg.solarmc.solarcredits.config;

import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.Map;

public interface RotatingShopConfig {

    @ConfKey("tebex.secret")
    String tebexSecret();

    @ConfKey("items")
    Map<String, @SubSection ItemConfig> items();

}
