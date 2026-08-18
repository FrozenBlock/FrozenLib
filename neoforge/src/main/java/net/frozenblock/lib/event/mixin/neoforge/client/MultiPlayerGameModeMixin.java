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

package net.frozenblock.lib.event.mixin.neoforge.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.frozenblock.lib.event.api.events.PlayerBlockBreakEvents;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(
		method = "destroyBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"
		),
		cancellable = true
	)
	private void frozenLib$breakBlock(
		BlockPos pos, CallbackInfoReturnable<Boolean> info,
		@Local(name = "level") Level level,
		@Local(name = "oldState") BlockState oldState,
		@Share("frozenLib$blockEntity") LocalRef<BlockEntity> blockEntity
	) {
		blockEntity.set(level.getBlockEntity(pos));
		boolean result = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, this.minecraft.player, pos, oldState, blockEntity.get());

		if (!result) {
			PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(level, this.minecraft.player, pos, oldState, blockEntity.get());

			info.setReturnValue(false);
		}
	}

	@WrapOperation(
		method = "destroyBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
		)
	)
	private void frozenLib$onBlockBroken(
		Block instance, LevelAccessor levelAccessor, BlockPos pos, BlockState state, Operation<Void> original,
		@Local(name = "level") Level level,
		@Share("frozenLib$blockEntity") LocalRef<BlockEntity> blockEntity
	) {
		original.call(instance, levelAccessor, pos, state);
		PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(level, this.minecraft.player, pos, state, blockEntity.get());
	}
}
