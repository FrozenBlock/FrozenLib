/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.particle;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.particle.client.options.WindParticleOptions;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FrozenLibParticleTypes {
	public static final ParticleType<WindParticleOptions> WIND = register(
		"wind", false, particleType -> WindParticleOptions.CODEC, particleType -> WindParticleOptions.STREAM_CODEC
	);

	public static void init() {}

	@NotNull
	private static SimpleParticleType register(@NotNull String name, boolean alwaysShow) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, FrozenLibConstants.id(name), FabricParticleTypes.simple(alwaysShow));
	}

	@NotNull
	private static SimpleParticleType register(@NotNull String name) {
		return register(name, false);
	}

	@NotNull
	private static <T extends ParticleOptions> ParticleType<T> register(
		String string,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> function,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> function2
	) {
		return register(FrozenLibConstants.id(string), alwaysShow, function, function2);
	}

	@NotNull
	private static <T extends ParticleOptions> ParticleType<T> register(
		ResourceLocation resourceLocation,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> function,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> function2
	) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, resourceLocation, new ParticleType<T>(alwaysShow) {
			@Override
			public @NotNull MapCodec<T> codec() {
				return function.apply(this);
			}

			@NotNull
			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return function2.apply(this);
			}
		});
	}
}
