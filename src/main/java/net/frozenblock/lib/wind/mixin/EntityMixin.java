/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
