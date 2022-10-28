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

package org.quiltmc.qsl.frozenblock.resource.loader.impl;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.jetbrains.annotations.ApiStatus;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public record DataPackLoadingContext(RegistryAccess.Writable registryManager,
                                     ResourceManager resourceManager) {
    /**
     * Loads the registries from the {@link #resourceManager() resource manager}.
     *
     * @return the dynamic ops
     */
    public DynamicOps<JsonElement> loadRegistries() {
        return RegistryOps.createAndLoad(JsonOps.INSTANCE, this.registryManager, this.resourceManager);
    }

    public DataResult<WorldGenSettings> loadGeneratorOptions(WorldGenSettings existing, DynamicOps<JsonElement> registryOps) {
        DataResult<JsonElement> encodedBaseOptions = WorldGenSettings.CODEC.encodeStart(registryOps, existing)
                .setLifecycle(Lifecycle.stable());
        return encodedBaseOptions.flatMap(jsonElement -> WorldGenSettings.CODEC.parse(registryOps, jsonElement));
    }

    public DataResult<WorldGenSettings> loadDefaultGeneratorOptions(DynamicOps<JsonElement> registryOps) {
        return this.loadGeneratorOptions(WorldPresets.createNormalWorldFromPreset(this.registryManager), registryOps);
    }
}
