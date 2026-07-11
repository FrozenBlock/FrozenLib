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

package net.frozenblock.lib.particle;

import net.frozenblock.lib.particle.options.ColoredSmokeParticleOptions;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

@ClientOnly
public class ColoredLargeSmokeParticle extends LargeSmokeParticle {
	private final float targetRColor;
	private final float startRColor;
	private final float targetBColor;
	private final float startBColor;
	private final float targetGColor;
	private final float startGColor;
	private final int colorLerpEndsAt;
	private int colorLerpTicks;

	protected ColoredLargeSmokeParticle(
		ClientLevel level,
		double x, double y, double z,
		double xa, double ya, double za,
		float rDifference, float gDifference, float bDifference,
		SpriteSet spriteSet
	) {
		super(level, x, y, z, xa, ya, za, spriteSet);
		this.targetRColor = this.rCol;
		this.rCol = this.startRColor = Math.clamp(this.targetRColor + rDifference, 0F, 1F);
		this.targetGColor = this.gCol;
		this.gCol = this.startGColor = Math.clamp(this.targetGColor + gDifference, 0F, 1F);
		this.targetBColor = this.bCol;
		this.bCol = this.startBColor = Math.clamp(this.targetBColor + bDifference, 0F, 1F);
		this.colorLerpEndsAt = this.lifetime / 2;
	}

	@Override
	public void tick() {
		this.colorLerpTicks += 1;
		super.tick();
	}

	@Override
	protected int getLightCoords(float partialTick) {
		final float colorLerp = Math.min((this.colorLerpTicks + partialTick), this.colorLerpEndsAt) / this.colorLerpEndsAt;
		this.rCol = Mth.lerp(colorLerp, this.startRColor, this.targetRColor);
		this.gCol = Mth.lerp(colorLerp, this.startGColor, this.targetGColor);
		this.bCol = Mth.lerp(colorLerp, this.startBColor, this.targetBColor);
		return super.getLightCoords(partialTick);
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
