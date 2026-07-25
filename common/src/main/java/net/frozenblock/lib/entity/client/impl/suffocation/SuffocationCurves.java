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

package net.frozenblock.lib.entity.client.impl.suffocation;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType.Keyframe;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType.RelativeMode;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public final class SuffocationCurves {

	private SuffocationCurves() {}

	public static float eval(List<Keyframe> curve, float fraction) {
		if (curve.isEmpty()) return fraction;
		Keyframe previous = null;
		for (Keyframe frame : curve) {
			if (fraction <= frame.fraction()) {
				if (previous == null) return frame.value();
				final float span = frame.fraction() - previous.fraction();
				final float t = span <= 0F ? 1F : Mth.clamp((fraction - previous.fraction()) / span, 0F, 1F);
				return Mth.lerp(t, previous.value(), frame.value());
			}
			previous = frame;
		}
		return previous.value();
	}

	public static float relativeScale(RelativeMode mode, float ownDanger, float[] allDangers) {
		return switch (mode) {
			case ABSOLUTE -> 1F;
			case RELATIVE_TO_TOTAL -> {
				float total = 0F;
				for (float danger : allDangers) total += danger;
				yield total <= 0F ? 0F : Mth.clamp(ownDanger / total, 0F, 1F);
			}
			case DOMINANT_ONLY -> {
				float max = 0F;
				for (float danger : allDangers) max = Math.max(max, danger);
				yield ownDanger > 0F && ownDanger >= max ? 1F : 0F;
			}
		};
	}

	public static float intensity(List<Keyframe> curve, RelativeMode mode, float ownDanger, float[] allDangers) {
		return Mth.clamp(eval(curve, ownDanger) * relativeScale(mode, ownDanger, allDangers), 0F, 1F);
	}
}
