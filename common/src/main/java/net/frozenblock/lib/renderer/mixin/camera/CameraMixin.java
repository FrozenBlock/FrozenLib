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

package net.frozenblock.lib.renderer.mixin.camera;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.renderer.impl.CameraRenderStateInterface;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public class CameraMixin {
	@Unique
	private static final Vector3f Y_AXIS_NEGATIVE = new Vector3f(0F, -1F, 0F);

	@ModifyExpressionValue(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;rotation()Lorg/joml/Quaternionf;"
		)
	)
	public Quaternionf frozenLib$extractHorizontalOrientation(Quaternionf original, CameraRenderState cameraState) {
		((CameraRenderStateInterface) cameraState).frozenLib$setHorizontalOrientation(Mth.rotationAroundAxis(Y_AXIS_NEGATIVE, original, new Quaternionf()));
		return original;
	}
}
