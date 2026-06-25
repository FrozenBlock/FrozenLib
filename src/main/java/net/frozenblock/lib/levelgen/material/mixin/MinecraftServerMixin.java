/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.material.mixin;

import java.util.Map;
import net.frozenblock.lib.levelgen.material.impl.SurfaceRuleUtil;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 2010) // apply after bclib
public abstract class MinecraftServerMixin {

	@Shadow
	public abstract RegistryAccess.Frozen registryAccess();

	@Shadow
	@Final
	private LayeredRegistryAccess<RegistryLayer> registries;

	@Inject(method = "createLevels", at = @At("TAIL"))
	private void frozenLib$addSurfaceRules(CallbackInfo info) {
		final RegistryAccess registryAccess = this.registries.compositeAccess();
		final Registry<LevelStem> levelStems = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);

		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : levelStems.entrySet()) {
			final LevelStem levelStem = entry.getValue();
			final ChunkGenerator chunkGenerator = levelStem.generator();
			if (!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator)) continue;

			final var noiseSettings = noiseGenerator.generatorSettings().value();
			final var dimension = levelStem.type().unwrapKey().orElseThrow();
			SurfaceRuleUtil.injectSurfaceRules(noiseSettings, registryAccess, dimension);
		}
	}

}
