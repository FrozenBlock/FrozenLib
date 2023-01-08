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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 996)
public class MinecraftServerMixin {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(CallbackInfo info) {
		MinecraftServer server = MinecraftServer.class.cast(this);
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> stemEntry : server.getWorldData().worldGenSettings().dimensions().entrySet()) {
			LevelStem stem = stemEntry.getValue();
			ChunkGenerator chunkGenerator = stem.generator();
			NoiseGeneratorInterface.class.cast(((NoiseBasedChunkGenerator)chunkGenerator).generatorSettings().value()).setDimension(stem.typeHolder());
		}
	}

}

