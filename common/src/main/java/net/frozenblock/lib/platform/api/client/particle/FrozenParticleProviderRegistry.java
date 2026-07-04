package net.frozenblock.lib.platform.api.client.particle;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@UtilityClass
public class FrozenParticleProviderRegistry {

	public static <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {
		FrozenLibInitPlatformUtils.PARTICLE_PROVIDER_REGISTRY.register(type, provider);
	}

	public static <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, PendingParticleProvider<T> provider) {
		FrozenLibInitPlatformUtils.PARTICLE_PROVIDER_REGISTRY.register(type, provider);
	}
}
