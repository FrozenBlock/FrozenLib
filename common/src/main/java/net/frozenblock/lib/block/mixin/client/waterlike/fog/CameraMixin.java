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

package net.frozenblock.lib.block.mixin.client.waterlike.fog;

import net.frozenblock.lib.block.client.impl.waterlike.WaterLikeFogUtil;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(Camera.class)
public abstract class CameraMixin {

	@Shadow
	private Level level;

	@Shadow
	public abstract FogType getFluidInCamera();

	@Shadow
	public abstract BlockPos blockPosition();

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/EnvironmentAttributeProbe;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;)V"
		)
	)
	private void frozenLib$tickWaterLikeFogHandler(CallbackInfo info) {
		WaterLikeFogUtil.tick(this.level, this.blockPosition(), this.getFluidInCamera(), false);
	}

	@Inject(method = "reset", at = @At("HEAD"))
	private void frozenLib$resetWaterLikeFog(CallbackInfo info) {
		WaterLikeFogUtil.reset(true);
	}

}
