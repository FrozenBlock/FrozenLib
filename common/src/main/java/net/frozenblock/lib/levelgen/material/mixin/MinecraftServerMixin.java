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

import net.frozenblock.lib.levelgen.material.api.MaterialRuleAdditions;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
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
	private void frozenLib$addRuleSources(CallbackInfo info) {
		final RegistryAccess registries = this.registries.compositeAccess();
		final Registry<LevelStem> levelStems = registries.lookupOrThrow(Registries.LEVEL_STEM);

		for (LevelStem levelStem : levelStems) {
			final ChunkGenerator chunkGenerator = levelStem.generator();
			if (!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator)) continue;

			final NoiseGeneratorSettings noiseGeneratorSettings = noiseBasedChunkGenerator.generatorSettings().value();
			final Holder<DimensionType> dimension = levelStem.type();

			MaterialRuleAdditions.compileAndGet(registries, dimension).ifPresent(noiseGeneratorSettings::frozenLib$setMaterialRuleAddition);
		}
	}
}
