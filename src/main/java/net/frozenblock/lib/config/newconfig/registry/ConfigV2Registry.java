package net.frozenblock.lib.config.newconfig.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.frozenblock.lib.config.newconfig.config.ConfigData;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.minecraft.resources.Identifier;
import java.util.Collection;
import java.util.Map;

public class ConfigV2Registry {
	public static final Map<ID, ConfigData<?>> CONFIG_DATA = new Object2ObjectOpenHashMap<>();
	public static final Map<ID, ConfigEntry<?>> CONFIG_ENTRY = new Object2ObjectOpenHashMap<>();

	public static void register(ConfigData data, ID id) {
		if (CONFIG_DATA.containsKey(id)) throw new IllegalStateException("A ConfigData with id " + id + " has already been registered!");
		if (CONFIG_DATA.containsValue(data)) throw new IllegalStateException("ConfigData " + data.id() + " has already been registered!");
		CONFIG_DATA.put(id, data);
	}

	public static void register(ConfigEntry entry, ID id) {
		if (CONFIG_ENTRY.containsKey(id)) throw new IllegalStateException("A ConfigEntry with id " + id + " has already been registered!");
		if (CONFIG_ENTRY.containsValue(entry)) throw new IllegalStateException("ConfigEntry " + entry.id() + " has already been registered!");
		CONFIG_ENTRY.put(id, entry);
	}

	public static ConfigData<?> getData(ID id) {
		return CONFIG_DATA.get(id);
	}

	public static ConfigData<?> getData(Identifier id) {
		return CONFIG_DATA.get(ID.of(id));
	}

	public static ConfigData<?> getData(String namespace, String path) {
		return CONFIG_DATA.get(ID.of(namespace, path));
	}

	public static Collection<ConfigData<?>> allConfigData() {
		return CONFIG_DATA.values();
	}

	public static ConfigEntry<?> getEntry(ID id) {
		return CONFIG_ENTRY.get(id);
	}

	public static ConfigEntry<?> getEntry(Identifier id) {
		return CONFIG_ENTRY.get(ID.of(id));
	}

	public static ConfigEntry<?> getEntry(String namespace, String path) {
		return CONFIG_ENTRY.get(ID.of(namespace, path));
	}

	public static Collection<ConfigEntry<?>> allConfigEntries() {
		return CONFIG_ENTRY.values();
	}
}
