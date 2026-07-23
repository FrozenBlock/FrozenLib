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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl;

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
public abstract class QuiltDataFixesInternals {
    private static final Logger LOGGER = LogUtils.getLogger();

    public record DataFixerEntry(DataFixer dataFixer, int currentVersion) {}

    @Range(from = 0, to = Integer.MAX_VALUE) // Changed to Optional & Dynamic by FrozenBlock
    public static Optional<Integer> getModDataVersion(Dynamic<?> dynamic, String modId) {
		final int version = dynamic.get(modId + "_DataVersion").asInt(-1);
		return version != -1 ? Optional.of(version) : Optional.empty();
    }

	@Range(from = 0, to = Integer.MAX_VALUE) // Changed to Optional & Dynamic by FrozenBlock
	public static Optional<Integer> getModDataVersion(CompoundTag tag, String modId) {
		return tag.getInt(modId + "_DataVersion");
	}

	@Range(from = 0, to = Integer.MAX_VALUE) // Changed to Optional & Dynamic by FrozenBlock
	public static Optional<Integer> getModMinecraftDataVersion(Dynamic<?> dynamic, String modId) {
		final int version = dynamic.get(modId + "_DataVersion_Minecraft").asInt(-1);
		return version != -1 ? Optional.of(version) : Optional.empty();
	}

	@Range(from = 0, to = Integer.MAX_VALUE) // Changed to Optional & Dynamic by FrozenBlock
	public static Optional<Integer> getModMinecraftDataVersion(CompoundTag tag, String modId) {
		return tag.getInt(modId + "_DataVersion_Minecraft");
	}

    private static QuiltDataFixesInternals instance;

    public static QuiltDataFixesInternals get() {
        if (instance != null) return  instance;

		Schema latestVanillaSchema;
		try {
			latestVanillaSchema = DataFixers.getDataFixer()
				.getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().dataVersion().version()));
		} catch (Exception e) {
			latestVanillaSchema = null;
		}

		if (latestVanillaSchema == null) {
			LOGGER.warn("[Quilt DFU API] Failed to initialize! Either someone stopped DFU from initializing,");
			LOGGER.warn("[Quilt DFU API] or this Minecraft build is hosed.");
			LOGGER.warn("[Quilt DFU API] Using no-op implementation.");
			instance = new NoOpQuiltDataFixesInternals();
		} else {
			instance = new QuiltDataFixesInternalsImpl(latestVanillaSchema);
		}

        return instance;
    }

    public abstract void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, DataFixer dataFixer);

	public abstract boolean isEmpty();

	@Nullable
    public abstract DataFixerEntry getFixerEntry(String modId);

	public abstract void registerMinecraftFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, DataFixer dataFixer);

	@Nullable
	public abstract DataFixerEntry getMinecraftFixerEntry(String modId);

    public abstract Schema createBaseSchema();

	public abstract void forEachFixer(Dynamic<?> dynamic, BiFunction<String, Integer, Integer> function);

	public abstract <T> Dynamic<T> updateWithAllFixers(DSL.TypeReference type, Dynamic<T> current, Optional<Map<String, Integer>> moddedDataVersions);

    public abstract CompoundTag addModDataVersions(CompoundTag tag);

	public abstract Dynamic<?> addModDataVersions(Dynamic<?> tag);

	public abstract void addModDataVersions(ValueOutput output);

    public abstract void freeze();

    public abstract boolean isFrozen();
}
