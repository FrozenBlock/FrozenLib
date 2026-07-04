package net.frozenblock.lib.platform.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.frozenblock.lib.platform.api.client.particle.PendingParticleProvider;
import net.frozenblock.lib.platform.service.ParticleProviderRegistryHelper;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import java.util.function.Supplier;

public class FabricParticleProviderRegistryHelper implements ParticleProviderRegistryHelper {

	@Override
	public <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {
		ParticleProviderRegistry.getInstance().register(type.get(), provider);
	}

	@Override
	public <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, PendingParticleProvider<T> provider) {
		ParticleProviderRegistry.getInstance().register(type.get(), spriteSet -> provider.create(spriteSet));
	}
}
