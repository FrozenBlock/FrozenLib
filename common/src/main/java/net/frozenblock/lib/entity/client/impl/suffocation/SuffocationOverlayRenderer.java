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
import net.frozenblock.lib.entity.client.impl.suffocation.ClientSuffocationState.Active;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType.ScreenEffectSettings;
import net.frozenblock.lib.platform.api.client.hud.HudElementRenderer;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;

@ClientOnly
public final class SuffocationOverlayRenderer implements HudElementRenderer {

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		final Minecraft minecraft = Minecraft.getInstance();
		final LocalPlayer player = minecraft.player;
		if (player == null || player.isSpectator()) return;

		final List<Active> active = ClientSuffocationState.active(player);
		if (active.isEmpty()) return;
		final float[] dangers = ClientSuffocationState.dangers(active);

		final int width = graphics.guiWidth();
		final int height = graphics.guiHeight();
		for (Active entry : active) {
			final ScreenEffectSettings settings = entry.type().screenEffect().orElse(null);
			if (settings == null || settings.overlayTexture().isEmpty()) continue;

			final float alpha = SuffocationCurves.intensity(settings.overlayAlphaCurve(), settings.relativeMode(), entry.danger(), dangers);
			if (alpha <= 0F) continue;

			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				settings.overlayTexture().get(),
				0, 0, width, height,
				ARGB.colorFromFloat(Math.min(alpha, 1F), 1F, 1F, 1F)
			);
		}
	}
}
