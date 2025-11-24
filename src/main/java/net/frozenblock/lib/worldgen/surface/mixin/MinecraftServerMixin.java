/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.surface.mixin;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.worldgen.surface.impl.OptimizedBiomeTagConditionSource;
import net.frozenblock.lib.worldgen.surface.impl.SurfaceRuleUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
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
	private void frozenLib$addSurfaceRules(CallbackInfo info) {
		final var server = MinecraftServer.class.cast(this);
		final var registryAccess = server.registryAccess();
		final var levelStems = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
		OptimizedBiomeTagConditionSource.INSTANCES.clear();

		for (var entry : levelStems.entrySet()) {
			final LevelStem stem = entry.getValue();
			final ChunkGenerator chunkGenerator = stem.generator();
			if (!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator)) continue;

			final var noiseSettings = noiseGenerator.generatorSettings().value();
			final var dimension = stem.type().unwrapKey().orElseThrow();
			SurfaceRuleUtil.injectSurfaceRules(noiseSettings, dimension);
		}

		OptimizedBiomeTagConditionSource.optimizeAll(this.registryAccess().lookupOrThrow(Registries.BIOME));
		FrozenLibLogUtils.log("Optimized tag source count: " + OptimizedBiomeTagConditionSource.INSTANCES.size(), FrozenLibConstants.UNSTABLE_LOGGING);
	}

}
