/*
 * Copyright 2023 The Quilt Project
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
 */

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl;

import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
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

    public abstract @NotNull Dynamic<Tag> updateWithAllFixers(@NotNull DataFixTypes dataFixTypes, @NotNull Dynamic<Tag> dynamic);

    public abstract @NotNull CompoundTag addModDataVersions(@NotNull CompoundTag compound);

    public abstract void freeze();

    @Contract(pure = true)
    public abstract boolean isFrozen();
}
