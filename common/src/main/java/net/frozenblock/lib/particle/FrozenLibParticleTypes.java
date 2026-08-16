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
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.ParticleTypeHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class FrozenLibParticleTypes {
	private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(
		Registries.PARTICLE_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<ControlledNoteParticleOptions>> CONTROLLED_NOTE = register("controlled_note",
		false,
		type -> ControlledNoteParticleOptions.CODEC,
		type -> ControlledNoteParticleOptions.STREAM_CODEC
	);
	public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> SMOKE_COLORED = register("smoke_colored",
		false,
		type -> ColoredSmokeParticleOptions.CODEC,
		type -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> LARGE_SMOKE_COLORED = register("large_smoke_colored",
		false,
		type -> ColoredSmokeParticleOptions.CODEC,
		type -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> CAMPFIRE_COSY_SMOKE_COLORED = register("campfire_cosy_smoke_colored",
		false,
		type -> ColoredSmokeParticleOptions.CODEC,
		type -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredSmokeParticleOptions>> CAMPFIRE_SIGNAL_SMOKE_COLORED = register("campfire_signal_smoke_colored",
		false,
		type -> ColoredSmokeParticleOptions.CODEC,
		type -> ColoredSmokeParticleOptions.STREAM_CODEC
	);
	public static final DeferredHolder<ParticleType<?>, ParticleType<WindParticleOptions>> WIND_SMALL = register("wind_small",
		false,
		type -> WindParticleOptions.CODEC,
		type -> WindParticleOptions.STREAM_CODEC
	);
	public static final DeferredHolder<ParticleType<?>, ParticleType<WindParticleOptions>> WIND_MEDIUM = register("wind_medium",
		false,
		type -> WindParticleOptions.CODEC,
		type -> WindParticleOptions.STREAM_CODEC
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name, boolean alwaysShow) {
		return REGISTER.register(name, () -> ParticleTypeHelper.simple(alwaysShow));
	}

	private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name) {
		return register(name, false);
	}

	private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
		String name,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> codec,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec
	) {
		return REGISTER.register(name, () -> ParticleTypeHelper.complex(alwaysShow, codec, streamCodec));
	}
}
