package net.frozenblock.lib.config.newconfig.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.frozenblock.lib.config.newconfig.config.ConfigData;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import java.util.Map;

public class ConfigV2Registry {
	public static final Map<ID, ConfigData<?>> CONFIG_DATA = new Object2ObjectOpenHashMap<>();

	public static final Map<ID, ConfigEntry<?>> CONFIG_ENTRY = new Object2ObjectOpenHashMap<>();
}
