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

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.debug.client.renderer.WindDebugRenderer;
import net.frozenblock.lib.debug.client.renderer.WindDisturbanceDebugRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.debug.DebugValueAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

	@Unique
	public final WindDebugRenderer frozenLib$windDebugRenderer = new WindDebugRenderer();
	@Unique
	public final WindDisturbanceDebugRenderer frozenLib$windDisturbanceDebugRenderer = new WindDisturbanceDebugRenderer();

	@Inject(method = "render", at = @At("TAIL"))
	private void frozenLib$render(
		PoseStack matrices, Frustum frustum, MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ,
		CallbackInfo info,
		@Local DebugValueAccess debugValueAccess
	) {
		if (FrozenBools.DEBUG_WIND) this.frozenLib$windDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ, debugValueAccess);
		if (FrozenBools.DEBUG_WIND_DISTURBANCES) this.frozenLib$windDisturbanceDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ, debugValueAccess);
	}

	@Inject(method = "clear", at = @At("TAIL"))
	private void frozenLib$clear(CallbackInfo info) {
		this.frozenLib$windDebugRenderer.clear();
		this.frozenLib$windDisturbanceDebugRenderer.clear();
	}
}
