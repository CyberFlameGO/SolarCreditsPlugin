package gg.solarmc.solarcredits.config.configs;

import space.arim.dazzleconf.annote.SubSection;

import java.util.List;
import java.util.Map;

public record LastRotateFileImpl(long lastRotateDay,
                                 Map<String, @SubSection PlayersInteracted> playersInteracted) implements LastRotateFile {
}
