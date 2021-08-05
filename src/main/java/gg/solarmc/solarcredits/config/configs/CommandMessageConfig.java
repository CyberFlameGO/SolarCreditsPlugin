package gg.solarmc.solarcredits.config.configs;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

public interface CommandMessageConfig {
    @ConfKey("successful")
    @ConfDefault.DefaultString("Command ran Successfully")
    String successful();

    @ConfKey("error")
    @ConfDefault.DefaultString("Command Failed")
    String error();
}
