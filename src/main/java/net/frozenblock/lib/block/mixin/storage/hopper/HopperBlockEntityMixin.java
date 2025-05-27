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

package net.frozenblock.lib.block.mixin.storage.hopper;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.frozenblock.lib.block.storage.api.hopper.HopperApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

	@ModifyExpressionValue(
		method = "ejectItems",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getAttachedContainer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;)Lnet/minecraft/world/Container;",
			ordinal = 0
		)
	)
	private static Container frozenLib$preventEjectionA(
		Container original,
		@Share("frozenLib$container") LocalRef<Container> containerRef
	) {
		containerRef.set(original);
		return original;
	}

	@Inject(
		method = "ejectItems",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getAttachedContainer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;)Lnet/minecraft/world/Container;",
			ordinal = 0,
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
    private static void frozenLib$preventEjectionB(
		Level level, BlockPos blockPos, HopperBlockEntity hopperBlockEntity, CallbackInfoReturnable<Boolean> info,
		@Share("frozenLib$container") LocalRef<Container> containerRef
	) {
        if (HopperApi.isContainerBlacklisted(containerRef.get())) info.setReturnValue(false);
    }

    @ModifyExpressionValue(
		method = "suckInItems",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getSourceContainer(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/Container;",
			ordinal = 0
		)
	)
    private static Container frozenLib$preventInsertionA(
		Container original,
		@Share("frozenLib$container") LocalRef<Container> containerRef
	) {
		containerRef.set(original);
		return original;
	}

	@Inject(
		method = "suckInItems",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getSourceContainer(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/Container;",
			ordinal = 0,
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	private static void frozenLib$preventInsertionB(
		Level level, Hopper hopper, CallbackInfoReturnable<Boolean> info,
		@Share("frozenLib$container") LocalRef<Container> containerRef
	) {
		if (HopperApi.isContainerBlacklisted(containerRef.get())) info.setReturnValue(false);
	}
}
