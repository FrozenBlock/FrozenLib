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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(SuspendedParticle.UnderwaterProvider.class)
public class SuspendedParticleUnderwaterProviderMixin {

	@WrapOperation(
		method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/SuspendedParticle;setColor(FFF)V"
		)
	)
	public void frozenLib$changeColorToWaterLike(
		SuspendedParticle instance, float r, float g, float b, Operation<Void> original,
		SimpleParticleType options, ClientLevel level, double x, double y, double z
	) {
		final BlockState state = level.getBlockState(BlockPos.containing(x, y, z));
		if (state.getBlock() instanceof WaterLikeBlock waterLikeBlock) {
			final int waterLikeColor = waterLikeBlock.waterLikeColor().rgba();
			r = ARGB.red(waterLikeColor) / 255F;
			g = ARGB.green(waterLikeColor) / 255F;
			b  = ARGB.blue(waterLikeColor) / 255F;
		}

		original.call(instance, r, g, b);
	}
}
