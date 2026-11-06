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

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.client.impl.waterlike.WaterLikeFogUtil;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
@Mixin(Camera.class)
public abstract class CameraMixin {

	@Shadow
	private Level level;

	@Shadow
	public abstract BlockPos blockPosition();

	@Shadow
	private FogType fogType;

	@Unique
	private @Nullable BlockState frozenLib$fluidFogBlockState;
	@Unique
	private FogType frozenLib$previousFogType;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/EnvironmentAttributeProbe;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;)V"
		)
	)
	private void frozenLib$tickWaterLikeFogHandler(CallbackInfo info) {
		WaterLikeFogUtil.tick(this.frozenLib$fluidFogBlockState, this.fogType, false);
	}

	@Inject(
		method = "calculateFogType",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/material/Fluid;getFogType()Lnet/minecraft/world/level/material/FogType;"
		)
	)
	private void frozenLib$clearFluidFogBlockState(CallbackInfoReturnable<FogType> info) {
		this.frozenLib$fluidFogBlockState = null;
	}

	@Inject(
		method = "calculateFogType",
		at = @At(
			value = "RETURN",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/level/material/Fluid;getFogType()Lnet/minecraft/world/level/material/FogType;"
			)
		)
	)
	private void frozenLib$captureFluidFogBlockState(
		CallbackInfoReturnable<FogType> info,
		@Local(name = "checkPos") BlockPos checkPos
	) {
		this.frozenLib$fluidFogBlockState = this.level.getBlockState(checkPos);
	}

	@Inject(method = "update", at = @At("RETURN"))
	public void frozenLib$resetOrInitializeWaterLikeFog(DeltaTracker deltaTracker, CallbackInfo info) {
		if (this.fogType != FogType.WATER || this.frozenLib$fluidFogBlockState == null) {
			WaterLikeFogUtil.reset(true);
		} else if (this.frozenLib$previousFogType != FogType.WATER) {
			WaterLikeFogUtil.tick(this.frozenLib$fluidFogBlockState, this.fogType, true);
		}

		this.frozenLib$previousFogType = this.fogType;
	}

	@Inject(method = "reset", at = @At("HEAD"))
	private void frozenLib$resetWaterLikeFog(CallbackInfo info) {
		WaterLikeFogUtil.reset(true);
	}
}
