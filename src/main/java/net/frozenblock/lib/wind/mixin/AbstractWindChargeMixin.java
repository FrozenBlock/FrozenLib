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

import net.frozenblock.lib.wind.api.WindDisturbingEntity;
import net.frozenblock.lib.wind.impl.WindDisturbance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractWindCharge.class)
public abstract class AbstractWindChargeMixin implements WindDisturbingEntity {
	@Unique
	private static final double FROZENLIB$WIND_RANGE_WIND_CHARGE = 5D;

	@Unique
	@Nullable
	@Override
	public WindDisturbance.DisturbanceLogic frozenLib$makeDisturbanceLogic() {
		return this::frozenLib$calculateBreezeWindAndWeight;
	}

	@Unique
	@Nullable
	private WindDisturbance.DisturbanceResult frozenLib$calculateBreezeWindAndWeight(Level level, @NotNull Vec3 windOrigin, AABB affectedArea, Vec3 windTarget) {
		double distance = windOrigin.distanceTo(windTarget);
		if (distance <= FROZENLIB$WIND_RANGE_WIND_CHARGE) {
			Vec3 chargeMovement = AbstractWindCharge.class.cast(this).getDeltaMovement();
			double strengthFromDistance = Mth.clamp((FROZENLIB$WIND_RANGE_WIND_CHARGE - distance) / (FROZENLIB$WIND_RANGE_WIND_CHARGE * 0.5D), 0D, 1D);
			Vec3 windVec = new Vec3(chargeMovement.x, chargeMovement.y, chargeMovement.z).scale(3D * strengthFromDistance);

			return new WindDisturbance.DisturbanceResult(
				strengthFromDistance,
				(FROZENLIB$WIND_RANGE_WIND_CHARGE - distance) * 2D,
				windVec
			);
		}
		return null;
	}

	@Unique
	@Override
	public double frozenLib$getWindWidth() {
		return 10D;
	}

	@Unique
	@Override
	public double frozenLib$getWindHeight() {
		return 10D;
	}

}
