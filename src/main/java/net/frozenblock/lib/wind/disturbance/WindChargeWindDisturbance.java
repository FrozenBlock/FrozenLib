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

package net.frozenblock.lib.wind.disturbance;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindChargeWindDisturbance extends EntityWindDisturbance<AbstractWindCharge> {
	public static WindChargeWindDisturbance INSTANCE = new WindChargeWindDisturbance();
	public static final MapCodec<WindChargeWindDisturbance> CODEC = MapCodec.unit(() -> INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, WindChargeWindDisturbance> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	private static final double AREA_WIDTH = 10D;
	private static final double AREA_HEIGHT = 10D;
	private static final double WIND_RANGE = 5D;

	@Override
	public AABB area(AbstractWindCharge source, Level level, Vec3 origin, Vec3 target, double scale) {
		return AABB.ofSize(
			origin,
			AREA_WIDTH * scale,
			AREA_HEIGHT * scale,
			AREA_WIDTH * scale
		);
	}

	@Override
	public WindDisturbanceResult get(AbstractWindCharge source, Level level, Vec3 origin, AABB area, Vec3 target, double scale) {
		final double scaledRange = WIND_RANGE * scale;
		final double distance = origin.distanceTo(target);
		if (distance > scaledRange) return WindDisturbanceResult.PASS;

		final Vec3 chargeMovement = source.getDeltaMovement();
		final double strengthFromDistance = Mth.clamp((scaledRange - distance) / (scaledRange * 0.5D), 0D, 1D);
		final Vec3 windVec = new Vec3(chargeMovement.x, chargeMovement.y, chargeMovement.z).scale(3D * strengthFromDistance);

		return WindDisturbanceResult.success(strengthFromDistance * scale,
			(scaledRange - distance) * 2D * scale,
			windVec
		);
	}

	@Override
	public WindDisturbanceType<?> type() {
		return WindDisturbanceType.WIND_CHARGE;
	}
}
