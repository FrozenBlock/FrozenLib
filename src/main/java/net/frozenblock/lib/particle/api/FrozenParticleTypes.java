package net.frozenblock.lib.particle.api;

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

public final class FrozenParticleTypes {
	public static final SimpleParticleType DEBUG_POS = register("debug_pos");

	private FrozenParticleTypes() {
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
