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

package net.frozenblock.lib.platform.service;

import net.frozenblock.lib.platform.api.client.particle.PendingParticleProvider;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import java.util.function.Supplier;

public interface ParticleProviderRegistryHelper {

	<T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, ParticleProvider<T> provider);

	<T extends ParticleOptions> void register(Supplier<ParticleType<T>> type, PendingParticleProvider<T> provider);
}
