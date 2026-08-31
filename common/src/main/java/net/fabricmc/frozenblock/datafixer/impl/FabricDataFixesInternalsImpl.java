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
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public final class FabricDataFixesInternalsImpl extends FabricDataFixesInternals {
    private final Schema latestVanillaSchema;
    private Map<String, List<DataFixerEntry>> modDataFixers;
    private boolean frozen;

    public FabricDataFixesInternalsImpl(Schema latestVanillaSchema) {
        this.latestVanillaSchema = latestVanillaSchema;
        this.modDataFixers = new Object2ReferenceOpenHashMap<>();
        this.frozen = false;
    }

    @Override
    public synchronized void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @Nullable String key, DataFixer dataFixer) {
        final List<DataFixerEntry> entries = this.modDataFixers.computeIfAbsent(modId, id -> new ObjectArrayList<>());
        if (entries.stream().anyMatch(entry -> Objects.equals(entry.key(), key))) {
            throw new IllegalArgumentException(
                "Mod '" + modId + "' already has a registered data fixer" + (key != null ? " with key '" + key + "'" : "")
            );
        }
        entries.add(new DataFixerEntry(dataFixer, currentVersion, key));
    }

	@Override
	public boolean isEmpty() {
		return this.modDataFixers.isEmpty();
	}

	@Override
	@Nullable
    public DataFixerEntry getFixerEntry(String modId, @Nullable String key) {
        final List<DataFixerEntry> entries = this.modDataFixers.get(modId);
        if (entries == null) return null;
        for (DataFixerEntry entry : entries) {
            if (Objects.equals(entry.key(), key)) return entry;
        }
        return null;
    }

	@Override
    public Schema createBaseSchema() {
        return new Schema(0, this.latestVanillaSchema);
    }

	@Override
	public void forEachFixer(Dynamic<?> dynamic, BiFunction<String, Integer, Integer> function) {
		for (Map.Entry<String, List<DataFixerEntry>> modEntry : this.modDataFixers.entrySet()) {
			for (DataFixerEntry entry : modEntry.getValue()) {
				// Changed to Optional by FrozenBlock
				final Optional<Integer> modDataVersion = getModDataVersion(dynamic, modEntry.getKey(), entry.key());

				// Check implemented by FrozenBlock for performance.
				// We recommend you register a DataFixer even if you don't need to fix anything currently to have a 100% success.
				if (modDataVersion.isEmpty()) continue;

				function.apply(storageKey(modEntry.getKey(), entry.key()), modDataVersion.get());
			}
		}
	}

	@Override
	public <T> Dynamic<T> updateWithAllFixers(DSL.TypeReference type, Dynamic<T> current, Optional<Map<String, Integer>> moddedDataVersions) {
		final boolean hasExistingDataVersions = !moddedDataVersions.isEmpty();

		for (Map.Entry<String, List<DataFixerEntry>> modEntry : this.modDataFixers.entrySet()) {
			for (DataFixerEntry entry : modEntry.getValue()) {
				// Changed to Optional by FrozenBlock
				final Optional<Integer> modDataVersion = hasExistingDataVersions
					? Optional.ofNullable(moddedDataVersions.get().get(storageKey(modEntry.getKey(), entry.key())))
					: getModDataVersion(current, modEntry.getKey(), entry.key());

				// Check implemented by FrozenBlock for performance.
				// We recommend you register a DataFixer even if you don't need to fix anything currently to have a 100% success.
				if (modDataVersion.isEmpty()) continue;

				current = entry.dataFixer().update(
					type,
					current,
					modDataVersion.get(),
					entry.currentVersion()
				);
			}
		}

		return current;
	}

    @Override
    public CompoundTag addModDataVersions(CompoundTag tag) {
        final CompoundTag dataVersions = tag.getCompoundOrEmpty(DATA_VERSIONS_KEY);
        for (Map.Entry<String, List<DataFixerEntry>> modEntry : this.modDataFixers.entrySet()) {
            for (DataFixerEntry entry : modEntry.getValue()) {
                dataVersions.putInt(storageKey(modEntry.getKey(), entry.key()), entry.currentVersion());
            }
        }
        tag.put(DATA_VERSIONS_KEY, dataVersions);
        return tag;
    }

	@Override
	public Dynamic<?> addModDataVersions(Dynamic<?> dynamic) {
		Dynamic<?> dataVersions = dynamic.get(DATA_VERSIONS_KEY).orElseEmptyMap();
		for (Map.Entry<String, List<DataFixerEntry>> modEntry : this.modDataFixers.entrySet()) {
			for (DataFixerEntry entry : modEntry.getValue()) {
				dataVersions = dataVersions.set(storageKey(modEntry.getKey(), entry.key()), dataVersions.createInt(entry.currentVersion()));
			}
		}
		return dynamic.set(DATA_VERSIONS_KEY, dataVersions);
	}

	@Override
	public void addModDataVersions(ValueOutput output) {
		final ValueOutput dataVersions = output.child(DATA_VERSIONS_KEY);
		for (Map.Entry<String, List<DataFixerEntry>> modEntry : this.modDataFixers.entrySet()) {
			for (DataFixerEntry entry : modEntry.getValue()) {
				dataVersions.putInt(storageKey(modEntry.getKey(), entry.key()), entry.currentVersion());
			}
		}
	}

	@Override
    public void freeze() {
        if (!this.frozen) {
            this.modDataFixers = Collections.unmodifiableMap(this.modDataFixers);
        }
        this.frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return this.frozen;
    }

}
