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

package net.frozenblock.lib.weather.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.tag.api.FrozenBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public final class LightningOverrideMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRainingAt(Lnet/minecraft/core/BlockPos;)Z"), method = "tickChunk")
	public boolean frozenLib$getLightningTarget(ServerLevel serverLevel, BlockPos pos, Operation<Boolean> operation) {
		return this.frozenLib$newLightningCheck(pos);
	}

	@Unique
	public boolean frozenLib$newLightningCheck(BlockPos position) {
		ServerLevel level = ServerLevel.class.cast(this);
		if (!level.isRaining() || !level.canSeeSky(position)) {
			return false;
		}
		if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) {
			return false;
		}
		Holder<Biome> biome = level.getBiome(position);
		return ((biome.value().hasPrecipitation() && biome.value().warmEnoughToRain(position)) || biome.is(FrozenBiomeTags.CAN_LIGHTNING_OVERRIDE)) && !biome.is(FrozenBiomeTags.CANNOT_LIGHTNING_OVERRIDE);
	}

}
