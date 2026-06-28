/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.client.waterlike;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.frozenblock.lib.platform.api.ClientOnly;import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@ClientOnly
@Mixin(ClientLevel.class)
public class ClientLevelMixin {

	@ModifyExpressionValue(
		method = "doAnimateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/material/FluidState;getDripParticle()Lnet/minecraft/core/particles/ParticleOptions;"
		)
	)
	public ParticleOptions frozenLib$useWaterLikeDripParticle(
		ParticleOptions original,
		@Local(name = "state") BlockState state,
		@Share("frozenLib$waterLikeBlock") LocalRef<WaterLikeBlock> waterLikeRef
	) {
		if (state.getBlock() instanceof WaterLikeBlock waterLikeBlock) {
			waterLikeRef.set(waterLikeBlock);
			return waterLikeBlock.dripParticle();
		}
		waterLikeRef.set(null);
		return original;
	}

	@WrapOperation(
		method = "doAnimateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/level/material/FluidState;getDripParticle()Lnet/minecraft/core/particles/ParticleOptions;"
			)
		)
	)
	public int frozenLib$useWaterLikeDripParticleChance(
		RandomSource instance, int i, Operation<Integer> original,
		@Share("frozenLib$waterLikeBlock") LocalRef<WaterLikeBlock> waterLikeRef
	) {
		return original.call(instance, waterLikeRef.get() != null ? waterLikeRef.get().dripParticleChance() : i);
	}

	@WrapOperation(
		method = "doAddParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/ParticleEngine;createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"
		)
	)
	private Particle frozenLib$replaceWithWaterLikeParticles(
		ParticleEngine instance, ParticleOptions options, double x, double y, double z, double xa, double ya, double za, Operation<Particle> original
	) {
		final ClientLevel level = ClientLevel.class.cast(this);
		if (options.equals(ParticleTypes.BUBBLE)) {
			final BlockState state = level.getBlockState(BlockPos.containing(x, y, z));
			if (state.getBlock() instanceof WaterLikeBlock waterLikeBlock) options = waterLikeBlock.bubbleParticle();
		} else if (options.equals(ParticleTypes.SPLASH)) {
			final BlockState state = level.getBlockState(BlockPos.containing(x, y, z));
			if (state.getBlock() instanceof WaterLikeBlock waterLikeBlock) options = waterLikeBlock.splashParticle();
		}

		return original.call(instance, options, x, y, z, xa, ya, za);
	}

}
