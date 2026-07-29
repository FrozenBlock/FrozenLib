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

package net.frozenblock.lib.levelgen.attribute.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.levelgen.attribute.api.FrozenLibEnvironmentAttributes;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@ClientOnly
@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {

	@ModifyExpressionValue(
		method = "extract",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Double;floatValue()F",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;"
			)
		)
	)
	public float frozenLib$applyLightmapBrightnessAttribute(
		float original,
		@Local(argsOnly = true) float partialTicks,
		@Local(name = "camera") Camera camera
	) {
		return original * camera.attributeProbe().getValue(FrozenLibEnvironmentAttributes.LIGHTMAP_BRIGHTNESS.get(), partialTicks);
	}
}
