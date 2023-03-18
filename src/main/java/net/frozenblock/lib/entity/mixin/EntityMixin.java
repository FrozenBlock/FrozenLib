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

package net.frozenblock.lib.entity.mixin;

import net.frozenblock.lib.entity.impl.EntityStepOnBlockInterface;
import net.frozenblock.lib.entity.impl.FrozenStartTrackingEntityInterface;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Entity.class)
public class EntityMixin implements FrozenStartTrackingEntityInterface, EntityStepOnBlockInterface {

	@Unique
	@Override
	public void frozenLib$playerStartsTracking(ServerPlayer serverPlayer) {
		Entity entity = Entity.class.cast(this);
		((EntityLoopingSoundInterface)entity).getSoundManager().syncWithPlayer(serverPlayer);
		((EntityLoopingFadingDistanceSoundInterface)entity).getFadingSoundManager().syncWithPlayer(serverPlayer);
		((EntitySpottingIconInterface)entity).getSpottingIconManager().sendIconPacket(serverPlayer);
		((EntityScreenShakeInterface)entity).getScreenShakeManager().syncWithPlayer(serverPlayer);
	}

	@Unique
	private BlockPos frozenLib$steppedPos;
	@Unique
	private BlockState frozenLib$steppedState;

	@ModifyArgs(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;stepOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;)V"))
	public void frozenLib$captureSteppedArgs(Args args) {
		this.frozenLib$steppedPos = args.get(1);
		this.frozenLib$steppedState = args.get(2);
	}

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;stepOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER))
	public void frozenLib$runSteppedOn(Args args) {
		this.frozenLib$onSteppedOnBlock(this.frozenLib$steppedPos, this.frozenLib$steppedState);
		this.frozenLib$steppedPos = null;
		this.frozenLib$steppedState = null;
	}

	@Override
	public void frozenLib$onSteppedOnBlock(BlockPos blockPos, BlockState blockState) {

	}
}
