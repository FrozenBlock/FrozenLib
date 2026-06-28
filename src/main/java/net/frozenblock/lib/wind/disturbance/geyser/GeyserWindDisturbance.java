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

package net.frozenblock.lib.wind.disturbance.geyser;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.wind.disturbance.WindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceResult;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GeyserWindDisturbance implements WindDisturbance<PotentSulfurBlockEntity> {
	public static final GeyserWindDisturbance INSTANCE = new GeyserWindDisturbance();
	public static final MapCodec<GeyserWindDisturbance> CODEC = MapCodec.unit(() -> INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, GeyserWindDisturbance> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	private static final WindDisturbanceResult ACTIVE_RESULT = WindDisturbanceResult.success(1D, 1D, new Vec3(0D, 0.2D, 0D).scale(40D));

	@Override
	public Vec3 origin(PotentSulfurBlockEntity source, Level level) {
		return Vec3.atCenterOf(source.getBlockPos());
	}

	@Override
	public AABB area(PotentSulfurBlockEntity source, Level level, Vec3 origin, Vec3 target, double scale) {
		return source.frozenLib$getWindArea();
	}

	@Override
	public WindDisturbanceResult get(PotentSulfurBlockEntity source, Level level, Vec3 target) {
		if (!source.frozenLib$isWindActive(level.getGameTime())) return WindDisturbanceResult.PASS;
		return WindDisturbance.super.get(source, level, target);
	}

	@Override
	public WindDisturbanceResult get(PotentSulfurBlockEntity source, Level level, Vec3 origin, AABB area, Vec3 target, double scale) {
		return ACTIVE_RESULT;
	}

	@Override
	public boolean expired(PotentSulfurBlockEntity source, Level level) {
		return source.isRemoved() || (!level.isClientSide() && !source.frozenLib$isWindActive(level.getGameTime()));
	}

	@Override
	public WindDisturbanceType<?> type() {
		return WindDisturbanceType.GEYSER;
	}
}
