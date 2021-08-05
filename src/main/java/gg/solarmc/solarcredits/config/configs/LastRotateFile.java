package gg.solarmc.solarcredits.config.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.List;
import java.util.Map;

public interface LastRotateFile {
    static Map<Integer, @SubSection PlayersInteracted> getDefault4Sets(PlayersInteracted players) {
        return Map.of(
                0, players,
                1, players,
                2, players,
                3, players
        );
    }

    @ConfKey("lastRotateDay")
    @ConfDefault.DefaultLong(0)
    @ConfComments({"Do not touch this :)", "This is the last day when the shop was rotated"})
    long lastRotateDay();

    @ConfKey("playersInteracted")
    @ConfDefault.DefaultObject("getDefault4Sets")
    Map<String, @SubSection PlayersInteracted> playersInteracted();
}
