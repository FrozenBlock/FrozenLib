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
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface WindDisturbance<T extends AttachmentTarget> {
	Codec<WindDisturbance<?>> CODEC = FrozenLibRegistries.WIND_DISTURBANCE_TYPE.byNameCodec().dispatch(WindDisturbance::type, WindDisturbanceType::codec);
	StreamCodec<RegistryFriendlyByteBuf, WindDisturbance<?>> STREAM_CODEC = ByteBufCodecs.registry(FrozenLibRegistries.WIND_DISTURBANCE_TYPE_REGISTRY)
		.dispatch(WindDisturbance::type, WindDisturbanceType::streamCodec);
	Codec<List<WindDisturbance<?>>> LIST_CODEC = CODEC.listOf();
	StreamCodec<RegistryFriendlyByteBuf, List<WindDisturbance<?>>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

	public default double scale(T source, Level level, Vec3 target) {
		return 1D;
	}

	public Vec3 origin(T source, Level level);

	public AABB area(T source, Level level, Vec3 origin, Vec3 target, double scale);

	public default AABB area(T source, Level level, Vec3 target) {
		return this.area(source, level, this.origin(source, level), target, this.scale(source, level, target));
	}

	public WindDisturbanceResult get(T source, Level level, Vec3 origin, AABB area, Vec3 target, double scale);

	public default WindDisturbanceResult get(T source, Level level, Vec3 target) {
		final double scale = this.scale(source, level, target);
		final Vec3 origin = this.origin(source, level);
		final AABB area = this.area(source, level, origin, target, scale);
		return get(source, level, origin, area, target, scale);
	}

	public boolean expired(T source, Level level);

	WindDisturbanceType<?> type();

	public record Tracked<T extends AttachmentTarget>(WindDisturbance<T> windDisturbance, T source) {
		public boolean isSourceValid(Level level) {
			source.
			if (this.source == null) return false;
			if (this.source instanceof Entity entity && (entity.isRemoved() || level != entity.level())) return false;
			if (this.source instanceof Chunk)
		}
	}
}
