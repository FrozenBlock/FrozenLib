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

package net.frozenblock.lib.item.mixin.bonemeal;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.item.api.bone_meal.BoneMealApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

	@Inject(
		method = "growCrop",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"
		),
		cancellable = true
	)
	private static void frozenLib$runBonemeal(
		ItemStack stack, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> info,
		@Local(ordinal = 0) BlockState state
	) {
		final BoneMealApi.BoneMealBehavior bonemealBehavior = BoneMealApi.get(state.getBlock());
		if (bonemealBehavior == null || !bonemealBehavior.meetsRequirements(level, pos, state)) return;

		if (level instanceof ServerLevel serverLevel) {
			if (bonemealBehavior.isBoneMealSuccess(level, level.getRandom(), pos, state)) {
				bonemealBehavior.performBoneMeal(serverLevel, level.getRandom(), pos, state);
			}
			stack.shrink(1);
		}
		info.setReturnValue(true);
	}

	@Inject(
		method = "addGrowthParticles",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"
		),
		cancellable = true
	)
	private static void frozenLib$addGrowthParticles(
		LevelAccessor level, BlockPos pos, int count, CallbackInfo info,
		@Local(ordinal = 0) BlockState state
	) {
		final BoneMealApi.BoneMealBehavior bonemealBehavior = BoneMealApi.get(state.getBlock());
		if (bonemealBehavior == null) return;

		final BlockPos particlePos = bonemealBehavior.getParticlePos(state, pos);
		if (bonemealBehavior.isNeighborSpreader()) {
			ParticleUtils.spawnParticles(level, particlePos, count, 3D, 1D, false, ParticleTypes.HAPPY_VILLAGER);
		} else {
			ParticleUtils.spawnParticleInBlock(level, particlePos, count, ParticleTypes.HAPPY_VILLAGER);
		}
		info.cancel();
	}

}
