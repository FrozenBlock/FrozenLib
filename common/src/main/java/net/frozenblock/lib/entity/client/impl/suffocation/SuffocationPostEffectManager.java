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
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entity.client.impl.suffocation.ClientSuffocationState.Active;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType.ScreenEffectSettings;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;

@ClientOnly
@UtilityClass
public final class SuffocationPostEffectManager {
	private static Identifier applied = null;

	public static void tick(Minecraft minecraft) {
		final GameRenderer gameRenderer = minecraft.gameRenderer;
		final Identifier desired = desiredPostEffect(minecraft.player);

		if (desired != null) {
			if (!desired.equals(applied) || !desired.equals(gameRenderer.currentPostEffect())) {
				gameRenderer.setPostEffect(desired);
				applied = desired;
			}
		} else if (applied != null) {
			if (applied.equals(gameRenderer.currentPostEffect())) gameRenderer.clearPostEffect();
			applied = null;
		}
	}

	private static Identifier desiredPostEffect(LocalPlayer player) {
		if (player == null || player.isSpectator()) return null;
		final List<Active> active = ClientSuffocationState.active(player);
		if (active.isEmpty()) return null;

		final float[] dangers = ClientSuffocationState.dangers(active);
		Identifier best = null;
		float bestIntensity = 0F;
		for (Active entry : active) {
			final ScreenEffectSettings settings = entry.type().screenEffect().orElse(null);
			if (settings == null || settings.postEffect().isEmpty()) continue;

			final float intensity = SuffocationCurves.intensity(settings.postIntensityCurve(), settings.relativeMode(), entry.danger(), dangers);
			if (intensity > bestIntensity) {
				bestIntensity = intensity;
				best = settings.postEffect().get();
			}
		}
		return best;
	}

	public static void reset() {
		applied = null;
	}
}
