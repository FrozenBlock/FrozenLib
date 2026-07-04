package net.frozenblock.lib.platform.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.client.particle.PendingParticleProvider;
import net.frozenblock.lib.platform.service.ParticleProviderRegistryHelper;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class NeoParticleProviderRegistryHelper implements ParticleProviderRegistryHelper {
	private record SpecialEntry<T extends ParticleOptions>(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {}

	private record SpriteEntry<T extends ParticleOptions>(Supplier<ParticleType<T>> type, PendingParticleProvider<T> provider) {}

	private static final List<SpecialEntry<?>> SPECIAL_ENTRIES = new ArrayList<>();
	private static final List<SpriteEntry<?>> SPRITE_ENTRIES = new ArrayList<>();

	@Override
	public <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {
		SPECIAL_ENTRIES.add(new SpecialEntry<>(type, provider));
	}

	@Override
	public <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, PendingParticleProvider<T> provider) {
		SPRITE_ENTRIES.add(new SpriteEntry<>(type, provider));
	}

	public static void flush(RegisterParticleProvidersEvent event) {
		for (SpecialEntry<?> entry : SPECIAL_ENTRIES) {
			flushSpecial(event, entry);
		}
		for (SpriteEntry<?> entry : SPRITE_ENTRIES) {
			flushSprite(event, entry);
		}
	}

	private static <T extends ParticleOptions> void flushSpecial(RegisterParticleProvidersEvent event, SpecialEntry<T> entry) {
		event.registerSpecial(entry.type().get(), entry.provider());
	}

	private static <T extends ParticleOptions> void flushSprite(RegisterParticleProvidersEvent event, SpriteEntry<T> entry) {
		event.registerSpriteSet(entry.type().get(), entry.provider()::create);
	}
}
