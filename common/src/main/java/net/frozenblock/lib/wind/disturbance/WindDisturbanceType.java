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

import com.mojang.serialization.Codec;
import java.util.List;
import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentTarget;
import net.frozenblock.lib.registry.FrozenLibBuiltInRegistries;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

// TODO: revamp completely i think
public interface WindDisturbanceType<T> {
	Codec<WindDisturbanceType<?>> CODEC = FrozenLibBuiltInRegistries.WIND_DISTURBANCE_TYPE.byNameCodec();
	StreamCodec<RegistryFriendlyByteBuf, WindDisturbanceType<?>> STREAM_CODEC = ByteBufCodecs.registry(FrozenLibRegistries.WIND_DISTURBANCE_TYPE);
	Codec<List<WindDisturbanceType<?>>> LIST_CODEC = CODEC.listOf();
	StreamCodec<RegistryFriendlyByteBuf, List<WindDisturbanceType<?>>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

	default double scale(T source, Level level) {
		return 1D;
	}

	Vec3 origin(T source, Level level);

	AABB area(T source, Level level, Vec3 origin, double scale);

	WindDisturbanceResult sample(T source, Level level, Vec3 origin, AABB area, Vec3 target, double scale);

	boolean expired(T source, Level level);

	default boolean invalidOrExpired(DataAttachmentTarget source, Level level) {
		try {
			return this.expired((T) source, level);
		} catch (Exception e) {
			FrozenLibLogUtils.logError("WindDisturbance invalid", e);
			return true;
		}
	}
	MapCodec<? extends WindDisturbanceType<T>> codec();

	StreamCodec<RegistryFriendlyByteBuf, ? extends WindDisturbanceType<T>> streamCodec();
}
