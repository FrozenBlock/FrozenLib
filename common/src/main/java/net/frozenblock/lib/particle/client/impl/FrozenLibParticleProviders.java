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

package net.frozenblock.lib.particle.client.impl;

import net.frozenblock.lib.particle.options.ControlledNoteParticleOptions;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ClientOnly
public final class FrozenLibParticleProviders {

	public static class NoteProvider implements ParticleProvider<ControlledNoteParticleOptions> {
		private final SpriteSet sprite;

		public NoteProvider(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(
			ControlledNoteParticleOptions options,
			ClientLevel level,
			double x, double y, double z,
			double xAux, double yAux, double zAux,
			RandomSource random
		) {
			return new NoteParticle(level, x, y, z, (options.note()) / 24D, this.sprite.get(random));
		}
	}
}
