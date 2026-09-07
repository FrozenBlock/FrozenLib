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

package net.frozenblock.lib.block.mixin.brushable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.block.api.NonFallingBrushableBlock;
import net.minecraft.world.level.block.BrushableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrushableBlock.class)
public class BrushableBlockMixin {

	@ModifyExpressionValue(
		method = "animateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"
		)
	)
	public int frozenLib$preventParticlesFromNonFallingBrushableBlocks(int original) {
		if (BrushableBlock.class.cast(this) instanceof NonFallingBrushableBlock) return 900;
		return original;
	}
}
