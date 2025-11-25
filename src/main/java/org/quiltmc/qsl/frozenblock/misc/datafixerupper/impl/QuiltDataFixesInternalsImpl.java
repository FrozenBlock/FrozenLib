/*
 * Copyright 2024-2025 The Quilt Project
 * Copyright 2024-2025 FrozenBlock
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
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin.DataFixTypesAccessor;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public final class QuiltDataFixesInternalsImpl extends QuiltDataFixesInternals {
    private final Schema latestVanillaSchema;

    private Map<String, DataFixerEntry> modDataFixers;
	private Map<String, DataFixerEntry> modMinecraftDataFixers;
    private boolean frozen;

    public QuiltDataFixesInternalsImpl(Schema latestVanillaSchema) {
        this.latestVanillaSchema = latestVanillaSchema;
        this.modDataFixers = new Object2ReferenceOpenHashMap<>();
		this.modMinecraftDataFixers = new Object2ReferenceOpenHashMap<>();
        this.frozen = false;
    }

    @Override
    public void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, DataFixer dataFixer) {
        if (this.modDataFixers.containsKey(modId)) throw new IllegalArgumentException("Mod '" + modId + "' already has a registered data fixer");
        this.modDataFixers.put(modId, new DataFixerEntry(dataFixer, currentVersion));
    }

	@Override
	public boolean isEmpty() {
		return this.modDataFixers.isEmpty() && this.modMinecraftDataFixers.isEmpty();
	}

	@Override
	@Nullable
    public DataFixerEntry getFixerEntry(String modId) {
        return modDataFixers.get(modId);
    }

	@Override
	public void registerMinecraftFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, DataFixer dataFixer) {
		if (this.modMinecraftDataFixers.containsKey(modId)) throw new IllegalArgumentException("Mod '" + modId + "' already has a registered Minecraft-version-based data fixer");
		this.modMinecraftDataFixers.put(modId, new DataFixerEntry(dataFixer, currentVersion));
	}

	@Override
	@Nullable
	public DataFixerEntry getMinecraftFixerEntry(String modId) {
		return modMinecraftDataFixers.get(modId);
	}

	@Override
    public Schema createBaseSchema() {
        return new Schema(0, this.latestVanillaSchema);
    }

    @Override
    public Dynamic<Tag> updateWithAllFixers(DataFixTypes dataFixTypes, Dynamic<Tag> current) {
		final  var tag = (CompoundTag) current.getValue();

		// Minecraft fixer added by FrozenBlock
		for (Map.Entry<String, DataFixerEntry> entry : this.modMinecraftDataFixers.entrySet()) {
			// Changed to Optional by FrozenBlock
			final Optional<Integer> modDataVersion = getModMinecraftDataVersion(tag, entry.getKey());
			final DataFixerEntry dataFixerEntry = entry.getValue();

			// Check implemented by FrozenBlock for performance.
			// We recommend you register a DataFixer even if you don't need to fix anything currently to have a 100% success.
			if (modDataVersion.isEmpty()) continue;
			current = dataFixerEntry.dataFixer().update(
				DataFixTypesAccessor.class.cast(dataFixTypes).getType(),
				current,
				modDataVersion.get(),
				dataFixerEntry.currentVersion()
			);
		}

        for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
			// Changed to Optional by FrozenBlock
			final Optional<Integer> modDataVersion = getModDataVersion(tag, entry.getKey());
			final DataFixerEntry dataFixerEntry = entry.getValue();

			// Check implemented by FrozenBlock for performance.
			// We recommend you register a DataFixer even if you don't need to fix anything currently to have a 100% success.
			if (modDataVersion.isEmpty()) continue;
			current = dataFixerEntry.dataFixer().update(
				DataFixTypesAccessor.class.cast(dataFixTypes).getType(),
				current,
				modDataVersion.get(),
				dataFixerEntry.currentVersion()
			);
        }

        return current;
    }

    @Override
    public CompoundTag addModDataVersions(CompoundTag tag) {
        for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
            tag.putInt(entry.getKey() + "_DataVersion", entry.getValue().currentVersion());
        }
		for (Map.Entry<String, DataFixerEntry> entry : this.modMinecraftDataFixers.entrySet()) {
			tag.putInt(entry.getKey() + "_DataVersion_Minecraft", entry.getValue().currentVersion());
		}
        return tag;
    }

	@Override
	public void addModDataVersions(ValueOutput output) {
		for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
			output.putInt(entry.getKey() + "_DataVersion", entry.getValue().currentVersion());
		}
		for (Map.Entry<String, DataFixerEntry> entry : this.modMinecraftDataFixers.entrySet()) {
			output.putInt(entry.getKey() + "_DataVersion_Minecraft", entry.getValue().currentVersion());
		}
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
