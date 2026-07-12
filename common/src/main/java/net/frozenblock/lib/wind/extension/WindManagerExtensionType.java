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

package net.frozenblock.lib.wind.extension;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface WindManagerExtensionType<E extends WindManagerExtension> {

	int priority();

	Supplier<E> supplier();

	MapCodec<E> codec();

	StreamCodec<RegistryFriendlyByteBuf, E> streamCodec();

	static void init() {}

	static <E extends WindManagerExtension> WindManagerExtensionType<E> register(
		Identifier id,
		int priority,
		Supplier<E> supplier,
		MapCodec<E> codec,
		StreamCodec<RegistryFriendlyByteBuf, E> streamCodec
	) {
		return Registry.register(FrozenLibRegistries.WIND_MANAGER_EXTENSION_TYPE, id, new WindManagerExtensionType<>() {
			@Override
			public int priority() {
				return priority;
			}

			@Override
			public Supplier<E> supplier() {
				return supplier;
			}

			@Override
			public MapCodec<E> codec() {
				return codec;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, E> streamCodec() {
				return streamCodec;
			}
		});
	}

	private static <E extends WindManagerExtension> WindManagerExtensionType<E> register(
		String name,
		int priority,
		Supplier<E> supplier,
		MapCodec<E> codec,
		StreamCodec<RegistryFriendlyByteBuf, E> streamCodec
	) {
		return register(FrozenLibConstants.id(name), priority, supplier, codec, streamCodec);
	}
}
