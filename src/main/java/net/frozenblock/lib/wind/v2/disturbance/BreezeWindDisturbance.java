/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.wind.v2.disturbance;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BreezeWindDisturbance implements WindDisturbance<Breeze> {
	public static BreezeWindDisturbance INSTANCE = new BreezeWindDisturbance();
	public static final MapCodec<BreezeWindDisturbance> CODEC = MapCodec.unit(() -> INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, BreezeWindDisturbance> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	private static final double AREA_WIDTH = 12D;
	private static final double AREA_HEIGHT = 10D;
	private static final double AREA_Y_OFFSET = 1D;
	private static final double WIND_RANGE = 6D;

	@Override
	public double scale(Breeze source, Level level, Vec3 target) {
		return source.getScale() * source.getAgeScale();
	}

	@Override
	public Vec3 origin(Breeze source, Level level) {
		return source.getBoundingBox().getCenter();
	}

	@Override
	public AABB area(Breeze source, Level level, Vec3 origin, Vec3 target, double scale) {
		return AABB.ofSize(
			origin,
			AREA_WIDTH * scale,
			AREA_HEIGHT * scale,
			AREA_WIDTH * scale
		).move(
			0D,
			AREA_Y_OFFSET * scale,
			0D
		);
	}

	@Nullable
	@Override
	public DisturbanceResult get(Breeze source, Level level, Vec3 origin, AABB area, Vec3 target, double scale) {
		final double scaledRange = WIND_RANGE * scale;
		final double distance = origin.distanceTo(target);
		if (distance > scaledRange) return null;

		final Vec3 breezeLookVec = source.getForward();
		final Vec3 differenceInPoses = origin.subtract(target);
		final double scaledDistance = (scaledRange - distance) / scaledRange;
		final double strengthFromDistance = Mth.clamp((scaledRange - distance) / (scaledRange * 0.75D), 0D, 1D);
		final double angleBetween = AdvancedMath.getAngleBetweenXZ(breezeLookVec, differenceInPoses) * Mth.DEG_TO_RAD;

		double x = Math.cos(angleBetween);
		double z = -Math.sin(angleBetween);
		x = -Mth.lerp(scaledDistance, (x - (differenceInPoses.x * 0.45D)) * 0.5D, x);
		z = -Mth.lerp(scaledDistance, (z - (differenceInPoses.z * 0.45D)) * 0.5D, z);

		final Vec3 windVec = new Vec3(x, strengthFromDistance, z);
		return new DisturbanceResult(
			strengthFromDistance * scale,
			(scaledRange - distance) * scale,
			windVec
		);
	}

	@Override
	public boolean expired(Breeze source, Level level) {
		return source.isRemoved();
	}

	@Override
	public WindDisturbanceType<?> type() {
		return WindDisturbanceType.BREEZE;
	}
}
