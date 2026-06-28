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

package net.frozenblock.lib.block.mixin.client.waterlike;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.frozenblock.lib.platform.api.ClientOnly;import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Inject(
		method = "extractShadowPiece",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;)I",
			shift = At.Shift.BEFORE
		),
		cancellable = true,
		require = 0
	)
	private static void frozenLib$stopShadowRenderingIfWaterLike(
		CallbackInfo info,
		@Local(name = "belowState") BlockState belowState
	) {
		if (belowState.getBlock() instanceof WaterLikeBlock) info.cancel();
	}

}
