package gg.solarmc.solarcredits.config.configs;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.List;

@FunctionalInterface
public interface PlayersInteracted {
    @ConfKey("players")
    @ConfDefault.DefaultStrings({})
    List<String> playerUUIDs();
}
