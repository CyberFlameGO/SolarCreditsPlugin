package gg.solarmc.solarcredits.config.configs;

import gg.solarmc.solarcredits.config.configs.CommandMessageConfig;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.Map;

public interface MessageConfig {

    static Map<String, @SubSection CommandMessageConfig> setDefaultConfig(@SubSection CommandMessageConfig defaultMsgConfig) {
        return Map.of(
                "add", defaultMsgConfig,
                "remove", defaultMsgConfig,
                "send", defaultMsgConfig,
                "set", defaultMsgConfig,
                "spend", defaultMsgConfig
        );
    }

    @ConfKey("playernotfound")
    @ConfDefault.DefaultString("The player is not found!!")
    String playerNotFound();

    @ConfKey("commands")
    @ConfDefault.DefaultObject("setDefaultConfig")
    @ConfComments({"{Variables} : ", "player : The name of the Player using the Command", "amount : The amount interacted with"})
    Map<String, @SubSection CommandMessageConfig> commandMsgs();
}
