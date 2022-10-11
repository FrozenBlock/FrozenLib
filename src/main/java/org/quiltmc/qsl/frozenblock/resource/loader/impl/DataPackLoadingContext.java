/*
 * Copyright 2022 QuiltMC
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
        return RegistryOps.createAndLoad(JsonOps.INSTANCE, this.registryManager,
                this.resourceManager);
    }

    public DataResult<WorldGenSettings> loadGeneratorOptions(
            WorldGenSettings existing, DynamicOps<JsonElement> registryOps) {
        DataResult<JsonElement> encodedBaseOptions =
                WorldGenSettings.CODEC.encodeStart(registryOps, existing)
                        .setLifecycle(Lifecycle.stable());
        return encodedBaseOptions.flatMap(
                jsonElement -> WorldGenSettings.CODEC.parse(registryOps,
                        jsonElement));
    }

    public DataResult<WorldGenSettings> loadDefaultGeneratorOptions(
            DynamicOps<JsonElement> registryOps) {
        return this.loadGeneratorOptions(
                WorldPresets.createNormalWorldFromPreset(this.registryManager),
                registryOps);
    }
}
