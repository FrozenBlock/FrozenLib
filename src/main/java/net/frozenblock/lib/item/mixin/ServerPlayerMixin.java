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

package net.frozenblock.lib.item.mixin;

import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.item.impl.SaveableItemCooldowns;
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

	@Unique
	public Optional<List<SaveableItemCooldowns.SaveableCooldownInstance>> frozenLib$savedItemCooldowns = Optional.empty();
	@Unique @Nullable
	private CompoundTag frozenLib$savedCooldownTag;

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void frozenLib$readAdditionalSaveData(CompoundTag compound, CallbackInfo info) {
		this.frozenLib$savedItemCooldowns = Optional.of(SaveableItemCooldowns.readCooldowns(compound));
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void frozenLib$addAdditionalSaveData(CompoundTag compound, CallbackInfo info) {
		SaveableItemCooldowns.saveCooldowns(compound, ServerPlayer.class.cast(this));
	}

	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void tick(CallbackInfo info) {
		if (this.frozenLib$savedItemCooldowns.isPresent() && this.connection != null && this.connection.isAcceptingMessages() && !this.isChangingDimension) {
			SaveableItemCooldowns.setCooldowns(this.frozenLib$savedItemCooldowns.get(), ServerPlayer.class.cast(this));
			this.frozenLib$savedItemCooldowns = Optional.empty();
		}
	}

	@Inject(method = "changeDimension", at = @At(value = "HEAD"))
	public void frozenLib$changeDimensionSaveCooldowns(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir) {
		CompoundTag tempTag = new CompoundTag();
		SaveableItemCooldowns.saveCooldowns(tempTag, ServerPlayer.class.cast(this));
		this.frozenLib$savedCooldownTag = tempTag;
	}

	@Inject(method = "changeDimension", at = @At(value = "RETURN"))
	public void frozenLib$changeDimensionLoadCooldowns(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir) {
		if (this.frozenLib$savedCooldownTag != null) {
			this.frozenLib$savedItemCooldowns = Optional.of(SaveableItemCooldowns.readCooldowns(this.frozenLib$savedCooldownTag));
		}
	}

}
