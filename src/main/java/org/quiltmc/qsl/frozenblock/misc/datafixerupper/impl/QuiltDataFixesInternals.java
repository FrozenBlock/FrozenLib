/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import org.jetbrains.annotations.*;
import org.slf4j.Logger;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public abstract class QuiltDataFixesInternals {
    private static final Logger LOGGER = LogUtils.getLogger();

    public record DataFixerEntry(DataFixer dataFixer, int currentVersion) {}

    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getModDataVersion(@NotNull CompoundTag compound, @NotNull String modId) {
        return compound.getInt(modId + "_DataVersion");
    }

    private static QuiltDataFixesInternals instance;

    public static @NotNull QuiltDataFixesInternals get() {
        if (instance == null) {
            Schema latestVanillaSchema;
            try {
                latestVanillaSchema = DataFixers.getDataFixer()
                        .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion()));
            } catch (Exception e) {
                latestVanillaSchema = null;
            }

            if (latestVanillaSchema == null) {
                LOGGER.warn("[Quilt DFU API] Failed to initialize! Either someone stopped DFU from initializing,");
                LOGGER.warn("[Quilt DFU API]  or this Minecraft build is hosed.");
                LOGGER.warn("[Quilt DFU API] Using no-op implementation.");
                instance = new NoOpQuiltDataFixesInternals();
            } else {
                instance = new QuiltDataFixesInternalsImpl(latestVanillaSchema);
            }
        }

        return instance;
    }

    public abstract void registerFixer(@NotNull String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
                                       @NotNull DataFixer dataFixer);

    public abstract @Nullable DataFixerEntry getFixerEntry(@NotNull String modId);

    @Contract(value = "-> new", pure = true)
    public abstract @NotNull Schema createBaseSchema();

    public abstract @NotNull CompoundTag updateWithAllFixers(@NotNull DataFixTypes dataFixTypes, @NotNull CompoundTag compound);

    public abstract @NotNull CompoundTag addModDataVersions(@NotNull CompoundTag compound);

    public abstract void freeze();

    @Contract(pure = true)
    public abstract boolean isFrozen();
}
