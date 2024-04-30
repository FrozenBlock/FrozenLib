/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.api.registry;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.sync.network.ConfigSyncData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class ConfigRegistry {

	private static final List<Config<?>> CONFIG_REGISTRY = new ObjectArrayList<>();
	private static final Map<String, List<Config<?>>> MOD_CONFIG_REGISTRY = new Object2ObjectOpenHashMap<>();

	private static final Map<String, List<TypedEntryType<?>>> MOD_TYPED_ENTRY_REGISTRY = new Object2ObjectOpenHashMap<>();
	private static final List<TypedEntryType<?>> TYPED_ENTRY_REGISTRY = new ObjectArrayList<>();

	private static final Map<Config<?>, Map<ConfigModification<?>, Integer>> MODIFICATION_REGISTRY = new Object2ObjectOpenHashMap<>();

	private static final Map<Config<?>, @Nullable ConfigSyncData<?>> CONFIG_SYNC_DATA = new Object2ObjectOpenHashMap<>();

	@NotNull
	@Contract("_ -> param1")
	public static <T> Config<T> register(Config<T> config) {
		if (CONFIG_REGISTRY.contains(config)) {
			throw new IllegalStateException("Config already registered.");
		}
		MOD_CONFIG_REGISTRY.computeIfAbsent(config.modId(), key -> new ArrayList<>()).add(config);
		CONFIG_REGISTRY.add(config);
		return config;
	}

	public static boolean contains(Config<?> config) {
		return CONFIG_REGISTRY.contains(config);
	}

	public static Collection<Config<?>> getConfigsForMod(String modId) {
		return Map.copyOf(MOD_CONFIG_REGISTRY).getOrDefault(modId, new ArrayList<>());
	}

	@Unmodifiable
	@Contract(pure = true)
	public static Collection<Config<?>> getAllConfigs() {
		return List.copyOf(CONFIG_REGISTRY);
	}

	@NotNull
	@Contract("_ -> param1")
	public static <T> TypedEntryType<T> register(TypedEntryType<T> entry) {
		if (TYPED_ENTRY_REGISTRY.contains(entry)) {
			throw new IllegalStateException("Typed entry already registered.");
		}
		MOD_TYPED_ENTRY_REGISTRY.computeIfAbsent(entry.modId(), key -> new ArrayList<>()).add(entry);
		TYPED_ENTRY_REGISTRY.add(entry);
		return entry;
	}

	public static boolean contains(TypedEntryType<?> entry) {
		return TYPED_ENTRY_REGISTRY.contains(entry);
	}

	public static Collection<TypedEntryType<?>> getTypedEntryTypesForMod(String modId) {
		return Map.copyOf(MOD_TYPED_ENTRY_REGISTRY).getOrDefault(modId, new ArrayList<>());
	}

	@Unmodifiable
	@Contract(pure = true)
	public static Collection<TypedEntryType<?>> getAllTypedEntryTypes() {
		return List.copyOf(TYPED_ENTRY_REGISTRY);
	}

	public static <T> ConfigModification<T> register(Config<T> config, ConfigModification<T> modification, int priority) {
		if (!contains(config)) throw new IllegalStateException("Config " + config + " not in registry!");
		MODIFICATION_REGISTRY.computeIfAbsent(config, a -> new Object2IntOpenHashMap<>()).put(modification, priority);
		return modification;
	}

	public static <T> ConfigModification<T> register(Config<T> config, ConfigModification<T> modification) {
		return register(config, modification, 1000);
	}

	public static <T> Map<ConfigModification<T>, Integer> getModificationsForConfig(Config<T> config) {
		return (Map<ConfigModification<T>, Integer>) (Map) MODIFICATION_REGISTRY.getOrDefault(config, new Object2IntOpenHashMap<>());
	}

	@ApiStatus.Internal
	@Nullable
	public static <T> ConfigSyncData<T> setSyncData(Config<T> config, @Nullable ConfigSyncData<T> data) {
		if (!contains(config)) throw new IllegalStateException("Config " + config + " not in registry!");
		CONFIG_SYNC_DATA.put(config, data);
		return data;
	}

	@ApiStatus.Internal
	@Nullable
	public static <T> ConfigSyncData<T> getSyncData(Config<T> config) {
		return (ConfigSyncData<T>) CONFIG_SYNC_DATA.get(config);
	}

	@ApiStatus.Internal
	public static <T> boolean containsSyncData(Config<T> config) {
		return CONFIG_SYNC_DATA.containsKey(config);
	}
}
