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

import lombok.experimental.UtilityClass;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;

/**
 * Disabled on this Minecraft version: {@code GameRenderer} no longer exposes a settable
 * "current post effect" (it rebuilds its requested-post-effects list internally every
 * render frame), so there is currently no supported way to apply a custom post-effect
 * shader here without a dedicated mixin. The other suffocation visuals (overlay, bubbles,
 * curves) are unaffected.
 */
@ClientOnly
@UtilityClass
public final class SuffocationPostEffectManager {
	// FIXME: implement on 26.3

	public static void tick(Minecraft minecraft) {}

	public static void reset() {}
}
