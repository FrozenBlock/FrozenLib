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

package net.frozenblock.lib.particle.client;

import net.frozenblock.lib.particle.client.api.ColoredLerpedParticleHelper;
import net.frozenblock.lib.particle.options.ColoredSmokeParticleOptions;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.RandomSource;

@ClientOnly
public class ColoredLargeSmokeParticle extends LargeSmokeParticle {
	final ColoredLerpedParticleHelper colorHelper;

	protected ColoredLargeSmokeParticle(
		ClientLevel level,
		double x, double y, double z,
		double xa, double ya, double za,
		float rDifference, float gDifference, float bDifference,
		SpriteSet spriteSet
	) {
		super(level, x, y, z, xa, ya, za, spriteSet);
		this.colorHelper = new ColoredLerpedParticleHelper(this, rDifference, gDifference, bDifference, this.lifetime / 2);
	}

	@Override
	public void tick() {
		this.colorHelper.tick();
		super.tick();
	}

	@Override
	public void extract(QuadParticleRenderState particleTypeRenderState, Camera camera, float partialTickTime) {
		this.colorHelper.applyColors(this, partialTickTime);
		super.extract(particleTypeRenderState, camera, partialTickTime);
	}

	public record Provider(SpriteSet spriteSet) implements ParticleProvider<ColoredSmokeParticleOptions> {
		@Override
		public Particle createParticle(
			ColoredSmokeParticleOptions options,
			ClientLevel level,
			double x, double y, double z,
			double xAux, double yAux, double zAux,
			RandomSource random
		) {
			return new ColoredLargeSmokeParticle(
				level,
				x, y, z,
				xAux, yAux, zAux,
				options.rDifference(), options.gDifference(), options.bDifference(),
				this.spriteSet
			);
		}
	}
}
