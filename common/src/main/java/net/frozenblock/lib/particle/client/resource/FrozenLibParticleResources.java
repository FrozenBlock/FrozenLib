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

package net.frozenblock.lib.particle.client.resource;

import net.frozenblock.lib.particle.ColoredCampfireSmokeParticle;
import net.frozenblock.lib.particle.ColoredLargeSmokeParticle;
import net.frozenblock.lib.particle.ColoredSmokeParticle;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.frozenblock.lib.particle.client.WindParticle;
import net.frozenblock.lib.particle.client.provider.FrozenLibParticleProviders;
import net.frozenblock.lib.platform.client.ParticleProviderRegistry;
import net.mehvahdjukaar.candlelight.api.ClientOnly;

@ClientOnly
public class FrozenLibParticleResources {

	public static void init() {
		ParticleProviderRegistry.register(FrozenLibParticleTypes.CONTROLLED_NOTE, FrozenLibParticleProviders.NoteProvider::new);
		ParticleProviderRegistry.register(FrozenLibParticleTypes.SMOKE_COLORED, ColoredSmokeParticle.Provider::new);
		ParticleProviderRegistry.register(FrozenLibParticleTypes.LARGE_SMOKE_COLORED, ColoredLargeSmokeParticle.Provider::new);
		ParticleProviderRegistry.register(FrozenLibParticleTypes.CAMPFIRE_COSY_SMOKE_COLORED, ColoredCampfireSmokeParticle.CosyProvider::new);
		ParticleProviderRegistry.register(FrozenLibParticleTypes.CAMPFIRE_SIGNAL_SMOKE_COLORED, ColoredCampfireSmokeParticle.SignalProvider::new);
		ParticleProviderRegistry.register(FrozenLibParticleTypes.WIND_SMALL, WindParticle.Provider::new);
		ParticleProviderRegistry.register(FrozenLibParticleTypes.WIND_MEDIUM, WindParticle.Provider::new);
	}
}
