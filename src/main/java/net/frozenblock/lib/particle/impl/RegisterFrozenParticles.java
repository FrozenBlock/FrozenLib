/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.particle.impl;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

public final class RegisterFrozenParticles {
	public static final SimpleParticleType DEBUG_POS = register("debug_pos");

	private RegisterFrozenParticles() {
		throw new UnsupportedOperationException("RegisterParticles contains only static declarations.");
	}

	public static void registerParticles() {
		FrozenSharedConstants.LOGGER.info("Registering Particles for " + FrozenSharedConstants.MOD_ID);
	}

	@NotNull
	private static SimpleParticleType register(@NotNull String name, boolean alwaysShow) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, FrozenSharedConstants.id(name), FabricParticleTypes.simple(alwaysShow));
	}

	@NotNull
	private static SimpleParticleType register(@NotNull String name) {
		return register(name, false);
	}

	@NotNull
	private static <T extends ParticleOptions> ParticleType<T> register(@NotNull String name, boolean alwaysShow, @NotNull ParticleOptions.Deserializer<T> factory, Function<ParticleType<T>, Codec<T>> codecGetter) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, FrozenSharedConstants.id(name), new ParticleType<T>(alwaysShow, factory) {
			@Override
			public Codec<T> codec() {
				return codecGetter.apply(this);
			}
		});
	}
}
