package gg.solarmc.solarcredits.config.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

@FunctionalInterface
public interface LastRotateConfig {
    @ConfKey("lastRotateDay")
    @ConfDefault.DefaultLong(0)
    @ConfComments({"Do not touch this :)", "This is the last day when the shop was rotated"})
    long lastRotateDay();
}
