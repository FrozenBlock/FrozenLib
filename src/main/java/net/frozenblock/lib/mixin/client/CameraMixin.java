/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.mixin.client;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.frozenblock.lib.screenshake.ScreenShaker;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

	@Shadow
	@Final
	private Quaternion rotation;
	@Shadow
	@Final
	private Vector3f left;
	@Unique
	private float zRot;

	@Inject(method = "setup", at = @At("RETURN"))
	private void setup(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
		ScreenShaker.cameraShake(Camera.class.cast(this), tickDelta);
		this.zRot = ScreenShaker.cameraZ(tickDelta);
	}

	@Inject(method = "setRotation", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Quaternion;set(FFFF)V", ordinal = 0))
	public void setRotation(float yRot, float xRot, CallbackInfo info) {
		this.rotation.mul(Vector3f.ZP.rotationDegrees(this.zRot));
	}

}
