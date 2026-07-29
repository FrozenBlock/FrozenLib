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

package net.frozenblock.lib.particle.client.api;

import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.Mth;

@ClientOnly
public final class ColoredLerpedParticleHelper {
	private final float targetRCol;
	private final float startRCol;
	private final float targetBCol;
	private final float startBCol;
	private final float targetGCol;
	private final float startGCol;
	private final int endTicks;
	private int tickCount;

	public ColoredLerpedParticleHelper(SingleQuadParticle particle, float rDifference, float gDifference, float bDifference, int endTicks) {
		this.targetRCol = particle.rCol;
		particle.rCol = this.startRCol = Math.clamp(this.targetRCol + rDifference, 0F, 1F);
		this.targetGCol = particle.gCol;
		particle.gCol = this.startGCol = Math.clamp(this.targetGCol + gDifference, 0F, 1F);
		this.targetBCol = particle.bCol;
		particle.bCol = this.startBCol = Math.clamp(this.targetBCol + bDifference, 0F, 1F);
		this.endTicks = endTicks;
	}

	public void tick() {
		this.tickCount += 1;
	}

	public void applyColors(SingleQuadParticle particle, float partialTicks) {
		final float colorLerp = Math.min((this.tickCount + partialTicks), this.endTicks) / this.endTicks;
		particle.rCol = Mth.lerp(colorLerp, this.startRCol, this.targetRCol);
		particle.gCol = Mth.lerp(colorLerp, this.startGCol, this.targetGCol);
		particle.bCol = Mth.lerp(colorLerp, this.startBCol, this.targetBCol);
	}
}
