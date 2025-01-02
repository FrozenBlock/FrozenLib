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

package net.frozenblock.lib.screenshake.mixin;

import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@Shadow
	public ServerGamePacketListenerImpl connection;
	@Shadow
	private boolean isChangingDimension;

	@Unique @Nullable
	private CompoundTag frozenLib$savedScreenShakesTag;
	@Unique
	private boolean frozenLib$hasSyncedScreenShakes = false;

	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void frozenLib$syncScreenShakes(CallbackInfo info) {
		EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface)ServerPlayer.class.cast(this)).frozenLib$getScreenShakeManager();
		if (!this.frozenLib$hasSyncedScreenShakes && this.connection != null && this.connection.isAcceptingMessages() && !this.isChangingDimension) {
			entityScreenShakeManager.syncWithPlayer(ServerPlayer.class.cast(this));
			this.frozenLib$hasSyncedScreenShakes = true;
		}
	}

	@Inject(method = "changeDimension", at = @At(value = "HEAD"))
	public void frozenLib$changeDimensionSaveScreenShakes(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir) {
		CompoundTag tempTag = new CompoundTag();
		EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface)ServerPlayer.class.cast(this)).frozenLib$getScreenShakeManager();
		entityScreenShakeManager.save(tempTag);
		this.frozenLib$savedScreenShakesTag = tempTag;
	}

	@Inject(method = "changeDimension", at = @At(value = "RETURN"))
	public void frozenLib$changeDimensionLoadScreenShakes(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir) {
		if (this.frozenLib$savedScreenShakesTag != null) {
			EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface)ServerPlayer.class.cast(this)).frozenLib$getScreenShakeManager();
			entityScreenShakeManager.load(this.frozenLib$savedScreenShakesTag);
			this.frozenLib$hasSyncedScreenShakes = false;
		}
	}

}
