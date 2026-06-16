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
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface WindDisturbanceType<D extends WindDisturbance<?>> {
	WindDisturbanceType<BreezeWindDisturbance> BREEZE = register("breeze", BreezeWindDisturbance.CODEC, BreezeWindDisturbance.STREAM_CODEC);
	WindDisturbanceType<WindChargeWindDisturbance> WIND_CHARGE = register("wind_charge", WindChargeWindDisturbance.CODEC, WindChargeWindDisturbance.STREAM_CODEC);

	MapCodec<D> codec();

	StreamCodec<RegistryFriendlyByteBuf, D> streamCodec();

	static void init() {}

	public static <D extends WindDisturbance<?>> WindDisturbanceType<D> register(
		Identifier id,
		MapCodec<D> codec,
		StreamCodec<RegistryFriendlyByteBuf, D> streamCodec
	) {
		return Registry.register(FrozenLibRegistries.WIND_DISTURBANCE_TYPE, id, new WindDisturbanceType<>() {
			@Override
			public MapCodec<D> codec() {
				return codec;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, D> streamCodec() {
				return streamCodec;
			}
		});
	}

	private static <D extends WindDisturbance<?>> WindDisturbanceType<D> register(
		String name,
		MapCodec<D> codec,
		StreamCodec<RegistryFriendlyByteBuf, D> streamCodec
	) {
		return register(FrozenLibConstants.id(name), codec, streamCodec);
	}
}
