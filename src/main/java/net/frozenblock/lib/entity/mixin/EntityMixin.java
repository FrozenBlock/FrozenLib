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

package net.frozenblock.lib.entity.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.entity.impl.EntityStepOnBlockInterface;
import net.frozenblock.lib.entity.impl.StartTrackingEntityInterface;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements StartTrackingEntityInterface, EntityStepOnBlockInterface {

	@Shadow
	public abstract Level level();

	@Unique
	@Override
	public void frozenLib$playerStartsTracking(ServerPlayer player) {
		final Entity entity = Entity.class.cast(this);
		((EntityLoopingSoundInterface)entity).frozenLib$getSoundManager().syncWithPlayer(player);
		((EntityLoopingFadingDistanceSoundInterface)entity).frozenLib$getFadingSoundManager().syncWithPlayer(player);
		((EntitySpottingIconInterface)entity).getSpottingIconManager().sendIconPacket(player);
		((EntityScreenShakeInterface)entity).frozenLib$getScreenShakeManager().syncWithPlayer(player);
	}

	@Inject(
		method = "Lnet/minecraft/world/entity/Entity;applyEffectsFromBlocks(Ljava/util/List;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/Block;stepOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;)V",
			shift = At.Shift.AFTER
		)
	)
	public void frozenLib$runSteppedOn(
		CallbackInfo info,
		@Local BlockPos pos, @Local BlockState state
	) {
		this.frozenLib$onSteppedOnBlock(this.level(), pos, state);
	}

	@Override
	public void frozenLib$onSteppedOnBlock(Level level, BlockPos pos, BlockState state) {
	}
}
