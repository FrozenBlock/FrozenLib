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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.frozenblock.lib.particle.ColoredCampfireSmokeParticle;
import net.frozenblock.lib.particle.ColoredLargeSmokeParticle;
import net.frozenblock.lib.particle.ColoredSmokeParticle;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.frozenblock.lib.particle.client.WindParticle;
import net.frozenblock.lib.particle.client.provider.FrozenLibParticleProviders;

@Environment(EnvType.CLIENT)
public class FrozenLibParticleResources {

	public static void init() {
		final ParticleProviderRegistry particleRegistry = ParticleProviderRegistry.getInstance();
		particleRegistry.register(FrozenLibParticleTypes.CONTROLLED_NOTE, FrozenLibParticleProviders.NoteProvider::new);
		particleRegistry.register(FrozenLibParticleTypes.SMOKE_COLORED, ColoredSmokeParticle.Provider::new);
		particleRegistry.register(FrozenLibParticleTypes.LARGE_SMOKE_COLORED, ColoredLargeSmokeParticle.Provider::new);
		particleRegistry.register(FrozenLibParticleTypes.CAMPFIRE_COSY_SMOKE_COLORED, ColoredCampfireSmokeParticle.CosyProvider::new);
		particleRegistry.register(FrozenLibParticleTypes.CAMPFIRE_SIGNAL_SMOKE_COLORED, ColoredCampfireSmokeParticle.SignalProvider::new);
		particleRegistry.register(FrozenLibParticleTypes.WIND_SMALL, WindParticle.Provider::new);
		particleRegistry.register(FrozenLibParticleTypes.WIND_MEDIUM, WindParticle.Provider::new);
	}
}
