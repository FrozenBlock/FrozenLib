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
import net.frozenblock.lib.registry.FrozenLibBuiltInRegistries;
import net.frozenblock.lib.wind.disturbance.holder.WindDisturbanceSourceType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import java.util.function.Supplier;

public abstract class WindDisturbanceType<T extends WindDisturbance<?>> {

	protected WindDisturbanceType() {}

	public static <T extends WindDisturbance<?>> WindDisturbanceType<T> create(
		MapCodec<T> codec,
		StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
		WindDisturbanceSourceType sourceType
	) {
		return new WindDisturbanceType<>() {

			@Override
			public WindDisturbanceSourceType sourceType() {
				return sourceType;
			}

			@Override
			public MapCodec<T> codec() {
				return codec;
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodec;
			}
		};
	}

	public static <T extends WindDisturbance<?>> Supplier<WindDisturbanceType<T>> createSupplier(
		MapCodec<T> codec,
		StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
		WindDisturbanceSourceType sourceType
	) {
		return () ->  create(codec, streamCodec, sourceType);
	}

	public abstract WindDisturbanceSourceType sourceType();

	public abstract MapCodec<T> codec();

	public abstract StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

	@Override
	public String toString() {
		return "WindDisturbanceType["
			+ FrozenLibBuiltInRegistries.WIND_DISTURBANCE_TYPE.getResourceKey(this)
				.map(key -> key.identifier().toString())
				.orElse("Unknown")
			+ "]";
	}
}
