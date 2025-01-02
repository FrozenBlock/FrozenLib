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

package net.frozenblock.lib.cape.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.cape.client.impl.PlayerCapeInterface;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(WingsLayer.class)
public class WingsLayerMixin {

	@ModifyExpressionValue(
		method = "getPlayerElytraTexture",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/PlayerSkin;capeTexture()Lnet/minecraft/resources/ResourceLocation;",
			ordinal = 0
		)
	)
	private static ResourceLocation frozenLib$captureAndChangeCapeLocation(
		ResourceLocation resourceLocation,
		@Local(ordinal = 0) PlayerRenderState playerRenderState,
		@Share("frozenLib$newCapeTexture") LocalRef<ResourceLocation> newCapeTexture
	) {
		if (playerRenderState instanceof PlayerCapeInterface capeInterface) {
			ResourceLocation capeTexture = capeInterface.frozenLib$getCape();
			if (capeTexture != null) {
				newCapeTexture.set(capeTexture);
				return capeTexture;
			}
		}
		return resourceLocation;
	}

	@ModifyExpressionValue(
		method = "getPlayerElytraTexture",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/PlayerSkin;capeTexture()Lnet/minecraft/resources/ResourceLocation;",
			ordinal = 1
		)
	)
	private static ResourceLocation frozenLib$useNewCape(
		ResourceLocation resourceLocation,
		@Share("frozenLib$newCapeTexture") LocalRef<ResourceLocation> newCapeTexture
	) {
		ResourceLocation capeTexture = newCapeTexture.get();
		if (capeTexture != null) return capeTexture;
		return resourceLocation;
	}
}
