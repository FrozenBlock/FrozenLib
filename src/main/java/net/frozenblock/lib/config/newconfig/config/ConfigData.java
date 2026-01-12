/*
 * Copyright (C) 2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.newconfig.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.frozenblock.lib.config.newconfig.ConfigSerializer;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.entry.EntryType;
import net.frozenblock.lib.config.newconfig.entry.property.EntryProperties;
import net.frozenblock.lib.config.newconfig.registry.ConfigV2Registry;
import net.frozenblock.lib.config.newconfig.registry.ID;
import net.frozenblock.lib.event.api.RegistryFreezeEvents;

public class ConfigData<T> {
	private final ID id;
	private final ConfigSettings<T> settings;
	private final Map<String, ConfigEntry<?>> entries = new Object2ObjectOpenHashMap<>();
	private final Map<String, Object> unoptimizedConfigMap = new Object2ObjectOpenHashMap<>();
	private final Map<ID, Object> optimizedConfigMap = new Object2ObjectOpenHashMap<>();
	public boolean loaded;
	public boolean optimizedMap;

	public ConfigData(ID id, ConfigSettings<T> settings) {
		this.id = id;
		this.settings = settings;
	}

	static {
		RegistryFreezeEvents.END_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (!allRegistries) return;
			ConfigV2Registry.allConfigData().forEach(ConfigData::optimizeConfigMap);
		});
	}

	public static <T> ConfigData<T> createAndRegister(ID id, ConfigSettings<T> settings) {
		final ConfigData<T> configData = new ConfigData<>(id, settings);
		ConfigV2Registry.register(configData, id);
		return configData;
	}

	public <B> ConfigEntry<B> entry(String id, EntryType<B> type, B defaultValue) {
		return (ConfigEntry<B>) this.entries.computeIfAbsent(id, id1 -> new ConfigEntry<>(this, id1, type, defaultValue, true, true));
	}

	public <B> ConfigEntry<B> unsyncableEntry(String id, EntryType<B> type, B defaultValue) {
		return (ConfigEntry<B>) this.entries.computeIfAbsent(id, id1 -> new ConfigEntry<>(this, id1, type, defaultValue, false, true));
	}

	public <B> ConfigEntry.Builder<B> entryBuilder(String id, EntryType<B> type, B defaultValue) {
		return entryBuilder(id, type, defaultValue, true, true);
	}

	public <B> ConfigEntry.Builder<B> unsyncableEntryBuilder(String id, EntryType<B> type, B defaultValue) {
		return entryBuilder(id, type, defaultValue, false, true);
	}

	public <B> ConfigEntry.Builder<B> entryBuilder(String id, EntryType<B> type, B defaultValue, boolean syncable, boolean modifiable) {
		return new ConfigEntry.Builder<B>(this).id(id).type(type).defaultValue(defaultValue).properties(EntryProperties.builderOf(syncable, modifiable));
	}

	public ID id() {
		return this.id;
	}

	public ConfigSettings<T> settings() {
		return this.settings;
	}

	public Map<String, ConfigEntry<?>> entries() {
		return this.entries;
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public void loadEntry(ConfigEntry entry, boolean checkIfCurrentlyLoaded) {
		this.load(checkIfCurrentlyLoaded);
		final Object value = this.optimizedMap
			? this.optimizedConfigMap.get(entry.id())
			: ConfigSerializer.getFromUnoptimizedDataMap(this, entry, this.unoptimizedConfigMap);
		if (value != null) entry.setValue(value);
	}

	public void optimizeConfigMap() {
		this.load(true);
		this.optimizedConfigMap.clear();
		this.optimizedConfigMap.putAll(ConfigSerializer.convertToOptimizedConfigMap(this, this.unoptimizedConfigMap));
		this.optimizedMap = true;
		this.unoptimizedConfigMap.clear();
	}

	public void load(boolean checkIfCurrentlyLoaded) {
		if (checkIfCurrentlyLoaded && this.loaded) return;
		this.unoptimizedConfigMap.clear();
		this.unoptimizedConfigMap.putAll(ConfigSerializer.loadConfigAsMap(this.id));
		this.loaded = true;
		if (this.optimizedMap) this.optimizeConfigMap();
	}

	public void save() {
		ConfigSerializer.saveConfig(this);
	}
}
