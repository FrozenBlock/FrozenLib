/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.wind.mixin;

import java.util.Optional;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.wind.api.WindDisturbingEntity;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.impl.WindDisturbingEntityImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements WindDisturbingEntity, WindDisturbingEntityImpl {

	@Inject(
		method = "baseTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;checkBelowWorld()V"
		)
	)
	public void frozenLib$addWindDisturbanceServer(CallbackInfo info) {
		if (this.level() instanceof ServerLevel serverLevel) {
			WindDisturbance windDisturbance = this.frozenLib$makeWindDisturbance();
			if (windDisturbance != null) {
				WindManager windManager = WindManager.getWindManager(serverLevel);
				if (this.frozenLib$useSyncPacket()) {
					windManager.addWindDisturbanceAndSync(windDisturbance);
				} else {
					windManager.addWindDisturbance(windDisturbance);
				}
			}
		}
	}

	@Unique
	@Nullable
	@Override
	public ResourceLocation frozenLib$getWindDisturbanceLogicID() {
		return null;
	}

	@Unique
	@Override
	public double frozenLib$getWindWidth() {
		return 2D;
	}

	@Unique
	@Override
	public double frozenLib$getWindHeight() {
		return 2D;
	}

	@Override
	public double frozenLib$getWindAreaYOffset() {
		return 0D;
	}

	@Unique
	@Nullable
	@Override
	public WindDisturbance frozenLib$makeWindDisturbance() {
		ResourceLocation disturbanceLogicID = this.frozenLib$getWindDisturbanceLogicID();
		if (disturbanceLogicID != null) {
			Optional<WindDisturbanceLogic<?>> disturbanceLogic = WindDisturbanceLogic.getWindDisturbanceLogic(disturbanceLogicID);
			if (disturbanceLogic.isPresent()) {
				Entity entity = Entity.class.cast(this);
				double scale = entity instanceof LivingEntity livingEntity ? livingEntity.getScale() : 1D;
				Vec3 position = entity.getBoundingBox().getCenter();
				return new WindDisturbance(
					Optional.of(entity),
					position,
					AABB.ofSize(
						position,
						this.frozenLib$getWindWidth() * scale,
						this.frozenLib$getWindHeight() * scale,
						this.frozenLib$getWindWidth() * scale
					).move(
						0D,
						this.frozenLib$getWindAreaYOffset() * scale,
						0D
					),
					disturbanceLogic.get()
				);
			}
		}
		return null;
	}

	@Unique
	@Override
	public boolean frozenLib$useSyncPacket() {
		return false;
	}

	@Shadow
	public Level level() {
		throw new AssertionError("Mixin injection failed - FrozenLib EntityMixin.");
	}

}
