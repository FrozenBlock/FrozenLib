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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.surface.mixin;

import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.worldgen.surface.impl.OptimizedBiomeTagConditionSource;
import net.frozenblock.lib.worldgen.surface.impl.SurfaceRuleUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 2010) // apply after bclib
public abstract class MinecraftServerMixin {

	@Shadow
	public abstract RegistryAccess.Frozen registryAccess();

	@Inject(method = "createLevels", at = @At("HEAD"))
	private void frozenLib$addSurfaceRules(ChunkProgressListener worldGenerationProgressListener, CallbackInfo ci) {
		var server = MinecraftServer.class.cast(this);
		var registryAccess = server.registryAccess();
		var levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

		OptimizedBiomeTagConditionSource.INSTANCES.clear();
		for (var entry : levelStems.entrySet()) {
			LevelStem stem = entry.getValue();
			ChunkGenerator chunkGenerator = stem.generator();

			if (chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
				var noiseSettings = noiseGenerator.generatorSettings().value();
				var dimension = stem.type().unwrapKey().orElseThrow();

				SurfaceRuleUtil.injectSurfaceRules(noiseSettings, dimension);
			}
		}

		OptimizedBiomeTagConditionSource.optimizeAll(this.registryAccess().registryOrThrow(Registries.BIOME));
		FrozenLogUtils.log("Optimized tag source count: " + OptimizedBiomeTagConditionSource.INSTANCES.size(), FrozenSharedConstants.UNSTABLE_LOGGING);
	}

}
