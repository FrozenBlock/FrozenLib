/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.frozenblock.lib.particle.client.WindParticle;

@Environment(EnvType.CLIENT)
public class FrozenLibParticleResources {

	public static void init() {
		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();

		particleRegistry.register(FrozenLibParticleTypes.WIND, WindParticle.Factory::new);
	}
}
