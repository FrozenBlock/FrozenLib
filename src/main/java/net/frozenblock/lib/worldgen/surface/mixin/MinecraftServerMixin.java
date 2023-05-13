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

package net.frozenblock.lib.worldgen.surface.mixin;

import java.util.Map;
import net.frozenblock.lib.worldgen.surface.impl.NoiseGeneratorInterface;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.frozenblock.lib.worldgen.surface.impl.SurfaceRuleUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 2010) // apply after bclib
public class MinecraftServerMixin {

	@Shadow
	@Final
	protected WorldData worldData;

	@Inject(method = "createLevels", at = @At("HEAD"))
	private void addSurfaceRules(ChunkProgressListener worldGenerationProgressListener, CallbackInfo ci) {
		var server = MinecraftServer.class.cast(this);
		var registryAccess = server.registryAccess();
		var levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

		for (var entry : levelStems.entrySet()) {
			LevelStem stem = entry.getValue();
			ChunkGenerator chunkGenerator = stem.generator();

			if (chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
				var noiseSettings = noiseGenerator.generatorSettings().value();
				var dimension = stem.type().unwrapKey().orElseThrow();

				SurfaceRuleUtil.injectSurfaceRules(noiseSettings, dimension);
			}
		}
	}

}
