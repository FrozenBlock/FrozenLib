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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements EntityScreenShakeInterface {

	@Unique
	public EntityScreenShakeManager frozenLib$entityScreenShakeManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void frozenLib$setScreenShakeManager(EntityType<? extends Entity> entityType, Level level, CallbackInfo info) {
		Entity entity = Entity.class.cast(this);
		this.frozenLib$entityScreenShakeManager = new EntityScreenShakeManager(entity);
	}

	@Inject(
		method = "saveWithoutId",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V",
			shift = At.Shift.AFTER
		)
	)
	public void frozenLib$saveScreenShakeData(ValueOutput output, CallbackInfo ci) {
		if (this.frozenLib$entityScreenShakeManager != null) {
			this.frozenLib$entityScreenShakeManager.save(output);
		}
	}

	@Inject(
		method = "load",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V",
			shift = At.Shift.AFTER
		)
	)
	public void frozenLib$loadScreenShakeData(ValueInput input, CallbackInfo ci) {
		this.frozenLib$entityScreenShakeManager.load(input);
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
	public EntityScreenShakeManager frozenLib$getScreenShakeManager() {
		return this.frozenLib$entityScreenShakeManager;
	}

	@Unique
	@Override
	public void frozenLib$addScreenShake(float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
		this.frozenLib$getScreenShakeManager().addShake(intensity, duration, durationFalloffStart, maxDistance, ticks);
	}
}
