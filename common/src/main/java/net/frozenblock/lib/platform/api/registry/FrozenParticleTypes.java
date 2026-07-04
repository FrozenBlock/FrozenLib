package net.frozenblock.lib.platform.api.registry;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

@UtilityClass
public class FrozenParticleTypes {

	public static SimpleParticleType simple() {
		return simple(false);
	}

	public static SimpleParticleType simple(boolean overrideLimiter) {
		return new SimpleParticleType(overrideLimiter) { };
	}

	public static <T extends ParticleOptions> ParticleType<T> complex(
		final MapCodec<T> codec,
		final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
	) {
		return complex(false, codec, streamCodec);
	}

	public static <T extends ParticleOptions> ParticleType<T> complex(
		boolean overrideLimiter,
		final MapCodec<T> codec,
		final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
	) {
		return new ParticleType<>(overrideLimiter) {
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

	public static <T extends ParticleOptions> ParticleType<T> complex(
		final Function<ParticleType<T>, MapCodec<T>> codecGetter,
		final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
	) {
		return complex(false, codecGetter, streamCodecGetter);
	}

	public static <T extends ParticleOptions> ParticleType<T> complex(
		boolean overrideLimiter,
		final Function<ParticleType<T>, MapCodec<T>> codecGetter,
		final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
	) {
		return new ParticleType<>(overrideLimiter) {
			@Override
			public MapCodec<T> codec() {
				return codecGetter.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodecGetter.apply(this);
			}
		};
	}
}
