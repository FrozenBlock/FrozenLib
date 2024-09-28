/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin.DataFixTypesAccessor;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public final class QuiltDataFixesInternalsImpl extends QuiltDataFixesInternals {
    private final @NotNull Schema latestVanillaSchema;

    private Map<String, DataFixerEntry> modDataFixers;
	private Map<String, DataFixerEntry> modMinecraftDataFixers;
    private boolean frozen;

    public QuiltDataFixesInternalsImpl(@NotNull Schema latestVanillaSchema) {
        this.latestVanillaSchema = latestVanillaSchema;

        this.modDataFixers = new Object2ReferenceOpenHashMap<>();
		this.modMinecraftDataFixers = new Object2ReferenceOpenHashMap<>();
        this.frozen = false;
    }

    @Override
    public void registerFixer(@NotNull String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @NotNull DataFixer dataFixer) {
        if (this.modDataFixers.containsKey(modId)) {
            throw new IllegalArgumentException("Mod '" + modId + "' already has a registered data fixer");
        }

        this.modDataFixers.put(modId, new DataFixerEntry(dataFixer, currentVersion));
    }

	@Override
	public boolean isEmpty() {
		return this.modDataFixers.isEmpty() && this.modMinecraftDataFixers.isEmpty();
	}

	@Override
    public @Nullable DataFixerEntry getFixerEntry(@NotNull String modId) {
        return modDataFixers.get(modId);
    }

	@Override
	public void registerMinecraftFixer(@NotNull String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @NotNull DataFixer dataFixer) {
		if (this.modMinecraftDataFixers.containsKey(modId)) {
			throw new IllegalArgumentException("Mod '" + modId + "' already has a registered Minecraft-version-based data fixer");
		}

		this.modMinecraftDataFixers.put(modId, new DataFixerEntry(dataFixer, currentVersion));
	}

	@Override
	public @Nullable DataFixerEntry getMinecraftFixerEntry(@NotNull String modId) {
		return modMinecraftDataFixers.get(modId);
	}

	@Override
    public @NotNull Schema createBaseSchema() {
        return new Schema(0, this.latestVanillaSchema);
    }

    @Override
    public @NotNull Dynamic<Tag> updateWithAllFixers(@NotNull DataFixTypes dataFixTypes, @NotNull Dynamic<Tag> current) {
        var compound = (CompoundTag) current.getValue();

		// Minecraft fixer added by FrozenBlock
		for (Map.Entry<String, DataFixerEntry> entry : this.modMinecraftDataFixers.entrySet()) {
			// Changed to OptionalInt by FrozenBlock
			OptionalInt modDataVersion = getModMinecraftDataVersion(compound, entry.getKey());
			DataFixerEntry dataFixerEntry = entry.getValue();

			// Check implemented by FrozenBlock for performance.
			// We recommend you register a DataFixer even if you don't need to fix anything currently to have a 100% success.
			int currentVersion = dataFixerEntry.currentVersion();
			if (modDataVersion.isPresent() || currentVersion == 1) {
				current = dataFixerEntry.dataFixer().update(
					DataFixTypesAccessor.class.cast(dataFixTypes).getType(),
					current,
					modDataVersion.getAsInt(),
					currentVersion
				);
			}
		}

        for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
			// Changed to OptionalInt by FrozenBlock
            OptionalInt modDataVersion = getModDataVersion(compound, entry.getKey());
            DataFixerEntry dataFixerEntry = entry.getValue();

			// Check implemented by FrozenBlock for performance.
			// We recommend you register a DataFixer even if you don't need to fix anything currently to have a 100% success.
			int currentVersion = dataFixerEntry.currentVersion();
			if (modDataVersion.isPresent() || currentVersion == 1) {
				current = dataFixerEntry.dataFixer().update(
					DataFixTypesAccessor.class.cast(dataFixTypes).getType(),
					current,
					modDataVersion.getAsInt(),
					currentVersion
				);
			}
        }

        return current;
    }

    @Override
    public @NotNull CompoundTag addModDataVersions(@NotNull CompoundTag compound) {
        for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
            compound.putInt(entry.getKey() + "_DataVersion", entry.getValue().currentVersion());
        }
		for (Map.Entry<String, DataFixerEntry> entry : this.modMinecraftDataFixers.entrySet()) {
			compound.putInt(entry.getKey() + "_DataVersion_Minecraft", entry.getValue().currentVersion());
		}

        return compound;
    }

	@Override
    public void freeze() {
        if (!this.frozen) {
            this.modDataFixers = Collections.unmodifiableMap(this.modDataFixers);
			this.modMinecraftDataFixers = Collections.unmodifiableMap(this.modMinecraftDataFixers);
        }

        this.frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return this.frozen;
    }

}
