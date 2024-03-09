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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements WindDisturbingEntity {

	@Inject(
		method = "baseTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;checkBelowWorld()V"
		)
	)
	public void frozenLib$baseTick(CallbackInfo info) {
		if (this.level() instanceof ServerLevel serverLevel) {
			WindDisturbance<?> inWorldWindModifier = this.frozenLib$makeWindDisturbance();
			if (inWorldWindModifier != null) {
				WindManager.getWindManager(serverLevel).addWindDisturbance(inWorldWindModifier);
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Unique
	@Nullable
	private WindDisturbance<?> frozenLib$makeWindDisturbance() {
		ResourceLocation disturbanceLogicID = this.frozenLib$getWindDisturbanceLogicID();
		if (disturbanceLogicID != null) {
			Optional<WindDisturbanceLogic<?>> disturbanceLogic = WindDisturbanceLogic.getWindDisturbanceLogic(disturbanceLogicID);
			if (disturbanceLogic.isPresent()) {
				Entity entity = Entity.class.cast(this);
				double scale = entity instanceof LivingEntity livingEntity ? livingEntity.getScale() : 1D;
				return new WindDisturbance(
					Optional.of(entity),
					entity.position(),
					AABB.ofSize(
						entity.getBoundingBox().getCenter(),
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

	@Shadow
	public Level level() {
		throw new AssertionError("Mixin injection failed - FrozenLib EntityMixin.");
	}

}
