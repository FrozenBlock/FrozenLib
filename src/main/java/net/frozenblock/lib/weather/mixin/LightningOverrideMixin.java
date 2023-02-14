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

import net.frozenblock.lib.tag.api.FrozenBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerLevel.class)
public final class LightningOverrideMixin {

	@Unique
	private BlockPos frozenLib$lightningPos;

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;findLightningTargetAround(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"), method = "tickChunk")
	public BlockPos frozenLib$getLightningTarget(BlockPos original) {
		this.frozenLib$lightningPos = original;
		return original;
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRainingAt(Lnet/minecraft/core/BlockPos;)Z"), method = "tickChunk")
	public boolean frozenLib$applyLightningOverride(boolean original) {
		if (this.frozenLib$lightningPos != null) {
			Holder<Biome> biome = ServerLevel.class.cast(this).getBiome(this.frozenLib$lightningPos);
			if (biome.is(FrozenBiomeTags.CAN_LIGHTNING_OVERRIDE)) {
				return true;
			} else if (biome.is(FrozenBiomeTags.CANNOT_LIGHTNING_OVERRIDE)) {
				return false;
			}
		}
		return original;
	}

}
