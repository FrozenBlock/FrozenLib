/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl;

import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin.DataFixTypesAccessor;

import java.util.Collections;
import java.util.Map;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public final class QuiltDataFixesInternalsImpl extends QuiltDataFixesInternals {
    private final @NotNull Schema latestVanillaSchema;

    private Map<String, DataFixerEntry> modDataFixers;
    private boolean frozen;

    public QuiltDataFixesInternalsImpl(@NotNull Schema latestVanillaSchema) {
        this.latestVanillaSchema = latestVanillaSchema;

        this.modDataFixers = new Object2ReferenceOpenHashMap<>();
        this.frozen = false;
    }

    @Override
    public void registerFixer(@NotNull String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
                              @NotNull DataFixer dataFixer) {
        if (this.modDataFixers.containsKey(modId)) {
            throw new IllegalArgumentException("Mod '" + modId + "' already has a registered data fixer");
        }

        this.modDataFixers.put(modId, new DataFixerEntry(dataFixer, currentVersion));
    }

    @Override
    public @Nullable DataFixerEntry getFixerEntry(@NotNull String modId) {
        return modDataFixers.get(modId);
    }

    @Override
    public @NotNull Schema createBaseSchema() {
        return new Schema(0, this.latestVanillaSchema);
    }

    @Override
    public @NotNull Dynamic<Tag> updateWithAllFixers(@NotNull DataFixTypes dataFixTypes, @NotNull Dynamic<Tag> current) {
        var compound = (CompoundTag) current.getValue();

        for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
            int modDataVersion = getModDataVersion(compound, entry.getKey());
            DataFixerEntry dataFixerEntry = entry.getValue();

			current = dataFixerEntry.dataFixer().update(
				DataFixTypesAccessor.class.cast(dataFixTypes).getType(),
				current,
				modDataVersion,
				dataFixerEntry.currentVersion()
			);
        }

        return current;
    }

    @Override
    public @NotNull CompoundTag addModDataVersions(@NotNull CompoundTag compound) {
        for (Map.Entry<String, DataFixerEntry> entry : this.modDataFixers.entrySet()) {
            compound.putInt(entry.getKey() + "_DataVersion", entry.getValue().currentVersion());
        }

        return compound;
    }

    @Override
    public void freeze() {
        if (!this.frozen) {
            modDataFixers = Collections.unmodifiableMap(this.modDataFixers);
        }

        this.frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return this.frozen;
    }

}
