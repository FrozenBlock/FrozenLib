/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.sound.api.FrozenClientPacketInbetween;
import net.frozenblock.lib.spotting_icon.api.SpottingIconManager;
import net.frozenblock.lib.spotting_icon.impl.EntitySpottingIconInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements EntitySpottingIconInterface {

	@Unique
	public SpottingIconManager frozenLib$SpottingIconManager;
	@Unique
	public boolean frozenLib$clientIconsSynced;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setIconManager(EntityType<?> entityType, Level level, CallbackInfo info) {
		Entity entity = Entity.class.cast(this);
		this.frozenLib$SpottingIconManager = new SpottingIconManager(entity);
	}


	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
	public void saveIconManager(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> info) {
		if (this.frozenLib$SpottingIconManager != null) {
			this.frozenLib$SpottingIconManager.save(compoundTag);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
	public void load(CompoundTag compoundTag, CallbackInfo info) {
		this.frozenLib$SpottingIconManager.load(compoundTag);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tickIcon(CallbackInfo info) {
		Entity entity = Entity.class.cast(this);
		if (!entity.level.isClientSide) {
			this.frozenLib$SpottingIconManager.tick();
		} else if (!this.frozenLib$clientIconsSynced) {
			FrozenClientPacketInbetween.requestFrozenIconSync(entity.getId(), entity.level.dimension());
			this.frozenLib$clientIconsSynced = true;
		}
	}

	@Unique
	@Override
	public boolean hasSyncedClient() {
		return this.frozenLib$clientIconsSynced;
	}

	@Unique
	@Override
	public SpottingIconManager getSpottingIconManager() {
		return this.frozenLib$SpottingIconManager;
	}

}
