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

package net.frozenblock.lib.screenshake.mixin;

import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeManager;
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
public class EntityMixin implements EntityScreenShakeInterface {

	@Unique
    public EntityScreenShakeManager frozenLib$entityScreenShakeManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void frozenLib$setScreenShakeManager(EntityType<? extends Entity> entityType, Level level, CallbackInfo info) {
        Entity entity = Entity.class.cast(this);
        this.frozenLib$entityScreenShakeManager = new EntityScreenShakeManager(entity);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    public void frozenLib$saveScreenShakeData(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> info) {
        if (this.frozenLib$entityScreenShakeManager != null) {
            this.frozenLib$entityScreenShakeManager.save(compoundTag);
        }
    }

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    public void frozenLib$loadScreenShaleData(CompoundTag compoundTag, CallbackInfo info) {
        this.frozenLib$entityScreenShakeManager.load(compoundTag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void frozenLib$tickScreenShake(CallbackInfo info) {
		Entity entity = Entity.class.cast(this);
        if (!entity.level().isClientSide) {
            this.frozenLib$entityScreenShakeManager.tick();
        }
    }

	@Unique
	@Override
	public EntityScreenShakeManager getScreenShakeManager() {
		return this.frozenLib$entityScreenShakeManager;
	}

	@Unique
	@Override
	public void addScreenShake(float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
		this.getScreenShakeManager().addShake(intensity, duration, durationFalloffStart, maxDistance, ticks);
	}
}
