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

package net.frozenblock.lib.particle.client.api.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.client.particle.PendingParticleProvider;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@ClientOnly
public final class ParticleProviderRegistryImpl {
	private static final List<SpecialEntry<?>> SPECIAL_ENTRIES = new ArrayList<>();
	private static final List<SpriteEntry<?>> SPRITE_ENTRIES = new ArrayList<>();

	public static <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {
		SPECIAL_ENTRIES.add(new SpecialEntry<>(type, provider));
	}

	public static <T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, PendingParticleProvider<T> provider) {
		SPRITE_ENTRIES.add(new SpriteEntry<>(type, provider));
	}

	public static void flush(RegisterParticleProvidersEvent event) {
		for (SpecialEntry<?> entry : SPECIAL_ENTRIES) flushSpecial(event, entry);
		for (SpriteEntry<?> entry : SPRITE_ENTRIES) flushSprite(event, entry);
	}

	private static <T extends ParticleOptions> void flushSpecial(RegisterParticleProvidersEvent event, SpecialEntry<T> entry) {
		event.registerSpecial(entry.type().get(), entry.provider());
	}

	private static <T extends ParticleOptions> void flushSprite(RegisterParticleProvidersEvent event, SpriteEntry<T> entry) {
		event.registerSpriteSet(entry.type().get(), entry.provider()::create);
	}

	private record SpecialEntry<T extends ParticleOptions>(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {}
	private record SpriteEntry<T extends ParticleOptions>(Supplier<ParticleType<T>> type, PendingParticleProvider<T> provider) {}
}
