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

import com.mojang.datafixers.util.Pair;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.wind.impl.InWorldWindModifier;
import net.frozenblock.lib.wind.api.WindDisturbingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Breeze.class)
public abstract class BreezeMixin implements WindDisturbingEntity {
	@Unique
	private static final double FROZENLIB$WIND_RANGE_BREEZE = 6D;

	@Unique
	@Nullable
	@Override
	public InWorldWindModifier.Modifier frozenLib$makeWindModifier() {
		return this::frozenLib$calculateBreezeWindAndWeight;
	}

	@Unique
	@Nullable
	private Pair<Pair<Double, Double>, Vec3> frozenLib$calculateBreezeWindAndWeight(Level level, @NotNull Vec3 windOrigin, AABB affectedArea, Vec3 windTarget) {
		double distance = windOrigin.distanceTo(windTarget);
		if (distance <= FROZENLIB$WIND_RANGE_BREEZE) {
			Vec3 breezeLookVec = Breeze.class.cast(this).getForward();
			Vec3 differenceInPoses = windOrigin.subtract(windTarget);
			double scaledDistance = (FROZENLIB$WIND_RANGE_BREEZE - distance) / FROZENLIB$WIND_RANGE_BREEZE;
			double strengthFromDistance = Mth.clamp((FROZENLIB$WIND_RANGE_BREEZE - distance) / (FROZENLIB$WIND_RANGE_BREEZE * 0.75D), 0D, 1D);
			double angleBetween = AdvancedMath.getAngleBetweenXZ(breezeLookVec, differenceInPoses);

			double x = Math.cos((angleBetween * Math.PI) / 180D);
			double z = -Math.sin((angleBetween * Math.PI) / 180D);
			x = Mth.lerp(scaledDistance, (x - (differenceInPoses.x * 0.45D)) * 0.5D, x);
			z = Mth.lerp(scaledDistance, (z - (differenceInPoses.z * 0.45D)) * 0.5D, z);

			Vec3 windVec = new Vec3(x, strengthFromDistance, z);
			return Pair.of(
				Pair.of(
					strengthFromDistance,
					FROZENLIB$WIND_RANGE_BREEZE - distance
				),
				windVec
			);
		}
		return null;
	}

	@Unique
	@Override
	public double frozenLib$getWindWidth() {
		return 12D;
	}

	@Unique
	@Override
	public double frozenLib$getWindHeight() {
		return 12D;
	}
}
