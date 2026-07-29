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
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentTarget;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface WindDisturbance<T> {
	Codec<WindDisturbance<?>> CODEC = FrozenLibRegistries.WIND_DISTURBANCE_TYPE.byNameCodec().dispatch(WindDisturbance::type, WindDisturbanceType::codec);
	StreamCodec<RegistryFriendlyByteBuf, WindDisturbance<?>> STREAM_CODEC = ByteBufCodecs.registry(FrozenLibRegistries.WIND_DISTURBANCE_TYPE_REGISTRY)
		.dispatch(WindDisturbance::type, WindDisturbanceType::streamCodec);
	Codec<List<WindDisturbance<?>>> LIST_CODEC = CODEC.listOf();
	StreamCodec<RegistryFriendlyByteBuf, List<WindDisturbance<?>>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

	default double scale(T source, Level level, Vec3 target) {
		return 1D;
	}

	Vec3 origin(T source, Level level);

	AABB area(T source, Level level, Vec3 origin, Vec3 target, double scale);

	default AABB area(T source, Level level, Vec3 target) {
		return this.area(source, level, this.origin(source, level), target, this.scale(source, level, target));
	}

	WindDisturbanceResult get(T source, Level level, Vec3 origin, AABB area, Vec3 target, double scale);

	default WindDisturbanceResult get(T source, Level level, Vec3 target) {
		final double scale = this.scale(source, level, target);
		final Vec3 origin = this.origin(source, level);
		final AABB area = this.area(source, level, origin, target, scale);
		if (!area.contains(target)) return WindDisturbanceResult.PASS;
		return get(source, level, origin, area, target, scale);
	}

	boolean expired(T source, Level level);

	default boolean invalidOrExpired(DataAttachmentTarget source, Level level) {
		try {
			return this.expired((T) source, level);
		} catch (Exception e) {
			FrozenLibLogUtils.logError("WindDisturbance invalid", e);
			return true;
		}
	}

	WindDisturbanceType<?> type();
}
