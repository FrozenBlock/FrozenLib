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

package net.frozenblock.lib.debug.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.debug.client.renderer.WindDebugRenderer;
import net.frozenblock.lib.debug.client.renderer.WindDisturbanceDebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

	@Shadow
	@Final
	private List<DebugRenderer.SimpleDebugRenderer> opaqueRenderers;

	@Shadow
	@Final
	private List<DebugRenderer.SimpleDebugRenderer> translucentRenderers;

	@Inject(method = "refreshRendererList", at = @At("TAIL"))
	private void frozenLib$render(CallbackInfo info) {
		if (FrozenLibConstants.DEBUG_WIND) this.opaqueRenderers.add(new WindDebugRenderer());
		if (FrozenLibConstants.DEBUG_WIND_DISTURBANCES) this.translucentRenderers.add(new WindDisturbanceDebugRenderer());
	}

}
