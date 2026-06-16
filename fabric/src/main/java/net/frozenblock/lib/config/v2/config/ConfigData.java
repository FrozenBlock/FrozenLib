/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.config.v2.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.v2.ConfigSerializer;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.entry.EntryType;
import net.frozenblock.lib.config.v2.entry.property.EntryProperties;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.lib.event.api.events.RegistryFreezeEvents;

public class ConfigData<T> {
	private final ID id;
	private final ConfigSettings<T> settings;
	private final Map<String, ConfigEntry<?>> entries = new Object2ObjectLinkedOpenHashMap<>();
	private final Map<String, T> unoptimizedConfigMap = new Object2ObjectLinkedOpenHashMap<>();
	private final Map<ID, T> optimizedConfigMap = new Object2ObjectLinkedOpenHashMap<>();
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

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
				ConfigV2Registry.allConfigData().forEach(ConfigData::save);
			});
		}

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
				ConfigV2Registry.allConfigData().forEach(ConfigData::save);
			});
		}
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

	public <B> ConfigEntry<B> entry(String id, ConfigEntry<B> entry) {
		return (ConfigEntry<B>) this.entries.put(id, entry);
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

	public <V> void loadEntry(ConfigEntry<V> entry, boolean checkIfCurrentlyLoaded) {
		this.load(checkIfCurrentlyLoaded);

		if (this.optimizedMap) {
			final T value = this.optimizedConfigMap.get(entry.id());
			if (value != null) {
				entry.setValueForLoad((V) value);
				this.optimizedConfigMap.remove(entry.id());
			}
		} else {
			ConfigSerializer.getFromUnoptimizedDataMap(this, entry, this.unoptimizedConfigMap)
				.ifLeft(entry::setValueForLoad)
				.ifRight(error -> FrozenLibLogUtils.logError("Failed to load entry " + entry.id() + ": " + error, FrozenLibLogUtils.UNSTABLE_LOGGING));
		}
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

		ConfigSerializer.<T>loadConfigAsMap(this.id)
			.ifLeft(this.unoptimizedConfigMap::putAll)
			.ifRight(error -> FrozenLibLogUtils.logError("Failed to load config " + this.id + ": " + error, FrozenLibLogUtils.UNSTABLE_LOGGING));

		this.loaded = true;
		if (this.optimizedMap) this.optimizeConfigMap();
	}

	public void save() {
		ConfigSerializer.saveConfig(this);
	}

	public void reload() {
		this.load(false);

		for (ConfigEntry<?> entry : this.entries.values()) {
			this.loadEntry(entry, true);
		}
	}
}
