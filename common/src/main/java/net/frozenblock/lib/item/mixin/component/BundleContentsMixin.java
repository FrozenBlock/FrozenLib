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

package net.frozenblock.lib.item.mixin.component;

import com.mojang.serialization.DataResult;
import net.frozenblock.lib.item.api.component.BundleWeightOverride;
import net.frozenblock.lib.item.api.component.FrozenLibDataComponents;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContents.class)
public class BundleContentsMixin {

	@Inject(method = "getWeight", at = @At("HEAD"), cancellable = true)
	private static void cameraPort$getFilmBundleWeight(ItemInstance item, CallbackInfoReturnable<DataResult<Fraction>> info) {
		if (item instanceof ItemStack stack) {
			final BundleWeightOverride weightOverride = stack.get(FrozenLibDataComponents.BUNDLE_WEIGHT_OVERRIDE.get());
			if (weightOverride == null) return;
			info.setReturnValue(DataResult.success(weightOverride.fraction()));
			return;
		}

		if (item instanceof ItemStackTemplate stackTemplate) {
			final BundleWeightOverride weightOverride = stackTemplate.get(FrozenLibDataComponents.BUNDLE_WEIGHT_OVERRIDE.get());
			if (weightOverride == null) return;
			info.setReturnValue(DataResult.success(weightOverride.fraction()));
			return;
		}
	}
}
