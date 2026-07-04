/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.particle.options.ColoredSmokeParticleOptions;
import net.frozenblock.lib.particle.options.ControlledNoteParticleOptions;
import net.frozenblock.lib.particle.options.WindParticleOptions;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.frozenblock.lib.platform.api.registry.FrozenParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class FrozenLibParticleTypes {
	private static final FrozenDeferredRegister<ParticleType<?>> REGISTER = FrozenDeferredRegister.create(
		Registries.PARTICLE_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final FrozenHolder<ParticleType<?>, ParticleType<ControlledNoteParticleOptions>> CONTROLLED_NOTE = register("controlled_note",
		false,
		particleType -> ControlledNoteParticleOptions.CODEC,
		particleType -> ControlledNoteParticleOptions.STREAM_CODEC
	);
	public static final FrozenHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> SMOKE_COLORED = register("smoke_colored",
		false,
		particleType -> ColoredSmokeParticleOptions.CODEC,
		particleType -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final FrozenHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> LARGE_SMOKE_COLORED = register("large_smoke_colored",
		false,
		particleType -> ColoredSmokeParticleOptions.CODEC,
		particleType -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final FrozenHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> CAMPFIRE_COSY_SMOKE_COLORED = register("campfire_cosy_smoke_colored",
		false,
		particleType -> ColoredSmokeParticleOptions.CODEC,
		particleType -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final FrozenHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> CAMPFIRE_SIGNAL_SMOKE_COLORED = register("campfire_signal_smoke_colored",
		false,
		particleType -> ColoredSmokeParticleOptions.CODEC,
		particleType -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final FrozenHolder<ParticleType<?>, ParticleType<WindParticleOptions>> WIND_SMALL = register("wind_small",
		false,
		particleType -> WindParticleOptions.CODEC,
		particleType -> WindParticleOptions.STREAM_CODEC
	);
	public static final FrozenHolder<ParticleType<?>, ParticleType<WindParticleOptions>> WIND_MEDIUM = register("wind_medium",
		false,
		particleType -> WindParticleOptions.CODEC,
		particleType -> WindParticleOptions.STREAM_CODEC
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static FrozenHolder<ParticleType<?>, SimpleParticleType> register(String name, boolean alwaysShow) {
		return REGISTER.register(name, () -> FrozenParticleTypes.simple(alwaysShow));
	}

	private static FrozenHolder<ParticleType<?>, SimpleParticleType> register(String name) {
		return register(name, false);
	}

	private static <T extends ParticleOptions> FrozenHolder<ParticleType<?>, ParticleType<T>> register(
		String id,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> codec,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec
	) {
		return REGISTER.register(id, () -> FrozenParticleTypes.complex(alwaysShow, codec, streamCodec));
	}
}
