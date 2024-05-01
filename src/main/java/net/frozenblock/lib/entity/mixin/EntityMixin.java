/*
 * Copyright (C) 2024 FrozenBlock
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

import net.frozenblock.lib.entity.impl.EntityStepOnBlockInterface;
import net.frozenblock.lib.entity.impl.FrozenStartTrackingEntityInterface;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin implements FrozenStartTrackingEntityInterface, EntityStepOnBlockInterface {

	@Shadow
	public abstract Level level();

	@Unique
	@Override
	public void frozenLib$playerStartsTracking(ServerPlayer serverPlayer) {
		Entity entity = Entity.class.cast(this);
		((EntityLoopingSoundInterface)entity).getSoundManager().syncWithPlayer(serverPlayer);
		((EntityLoopingFadingDistanceSoundInterface)entity).getFadingSoundManager().syncWithPlayer(serverPlayer);
		((EntitySpottingIconInterface)entity).getSpottingIconManager().sendIconPacket(serverPlayer);
		((EntityScreenShakeInterface)entity).getScreenShakeManager().syncWithPlayer(serverPlayer);
	}

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;stepOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void frozenLib$runSteppedOn(MoverType type, Vec3 pos, CallbackInfo ci, Vec3 vec3, double d, boolean bl, boolean bl2, BlockPos blockPos, BlockState blockState, Block block) {
		this.frozenLib$onSteppedOnBlock(this.level(), blockPos, blockState);
	}

	@Override
	public void frozenLib$onSteppedOnBlock(Level level, BlockPos blockPos, BlockState blockState) {

	}
}
