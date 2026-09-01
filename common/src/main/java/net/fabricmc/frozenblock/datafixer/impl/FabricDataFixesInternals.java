/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
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
 */

package net.fabricmc.frozenblock.datafixer.impl;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;
import net.minecraft.SharedConstants;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public abstract class FabricDataFixesInternals {
    private static final Logger LOGGER = LogUtils.getLogger();

	protected static final String DATA_VERSIONS_KEY = "_FabricDataVersions";

    public record DataFixerEntry(DataFixer dataFixer, int currentVersion, @Nullable String key) {}

	static String storageKey(String modId, @Nullable String key) {
		return key != null ? modId + '_' + key : modId;
	}

    @Range(from = 0, to = Integer.MAX_VALUE) // Changed to Optional & Dynamic by FrozenBlock
    public static Optional<Integer> getModDataVersion(Dynamic<?> dynamic, String modId, @Nullable String key) {
		final String storageKey = storageKey(modId, key);

		// LEGACY
		final int legacyVersion = dynamic.get(modId + "_DataVersion" + (key != null ? "_" + key : "")).asInt(-1);
		if (legacyVersion != -1) return Optional.of(legacyVersion);

		// GROUPED
		final int version = dynamic.get(DATA_VERSIONS_KEY).get(storageKey).asInt(-1);
		return version != -1 ? Optional.of(version) : Optional.empty();
    }

	@Range(from = 0, to = Integer.MAX_VALUE) // Changed to Optional & Dynamic by FrozenBlock
	public static Optional<Integer> getModDataVersion(CompoundTag tag, String modId, @Nullable String key) {
		final String storageKey = storageKey(modId, key);

		// LEGACY
		final String legacyKey = modId + "_DataVersion" + (key != null ? "_" + key : "");
		if (tag.contains(legacyKey)) return tag.getInt(legacyKey);

		// GROUPED
		return tag.getCompound(DATA_VERSIONS_KEY).flatMap(dataVersions -> dataVersions.getInt(storageKey));
	}

    private static volatile FabricDataFixesInternals instance;

    public static synchronized FabricDataFixesInternals get() {
        if (instance != null) return  instance;

		Schema latestVanillaSchema;
		try {
			latestVanillaSchema = DataFixers.getDataFixer()
				.getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().dataVersion().version()));
		} catch (Exception e) {
			latestVanillaSchema = null;
		}

		if (latestVanillaSchema == null) {
			LOGGER.warn("[Fabric DFU API] Failed to initialize! Either someone stopped DFU from initializing,");
			LOGGER.warn("[Fabric DFU API] or this Minecraft build is hosed.");
			LOGGER.warn("[Fabric DFU API] Using no-op implementation.");
			instance = new NoOpFabricDataFixesInternals();
		} else {
			instance = new FabricDataFixesInternalsImpl(latestVanillaSchema);
		}

        return instance;
    }

    public abstract void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @Nullable String key, DataFixer dataFixer);

	public abstract boolean isEmpty();

	@Nullable
    public abstract DataFixerEntry getFixerEntry(String modId, @Nullable String key);

    public abstract Schema createBaseSchema();

	public abstract void forEachFixer(Dynamic<?> dynamic, BiFunction<String, Integer, Integer> function);

	public abstract void forEachFixer(BiFunction<String, Integer, Integer> function);

	public abstract <T> Dynamic<T> updateWithAllFixers(DSL.TypeReference type, Dynamic<T> current, Optional<Map<String, Integer>> moddedDataVersions);

    public abstract CompoundTag addModDataVersions(CompoundTag tag);

	public abstract Dynamic<?> addModDataVersions(Dynamic<?> tag);

	public abstract void addModDataVersions(ValueOutput output);

    public abstract void freeze();

    public abstract boolean isFrozen();
}
