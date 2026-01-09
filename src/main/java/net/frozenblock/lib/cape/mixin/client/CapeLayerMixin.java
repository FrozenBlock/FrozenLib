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

package net.frozenblock.lib.cape.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.cape.client.impl.AvatarCapeInterface;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CapeLayer.class)
public class CapeLayerMixin {

	@Inject(
		method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;skin:Lnet/minecraft/world/entity/player/PlayerSkin;"
		)
	)
	private static void frozenLib$captureFrozenLibCape(
		PoseStack poseStack, SubmitNodeCollector collector, int i, AvatarRenderState renderState, float f, float g, CallbackInfo info,
		@Share("frozenLib$newCapeTexture") LocalRef<ClientAsset.Texture> newCapeAssetRef
	) {
		if (!(renderState instanceof AvatarCapeInterface capeInterface)) return;
		newCapeAssetRef.set(capeInterface.frozenLib$getCape());
	}

	@ModifyExpressionValue(
		method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/PlayerSkin;cape()Lnet/minecraft/core/ClientAsset$Texture;"
		)
	)
	public ClientAsset.Texture frozenLib$useFrozenLibCape(
		ClientAsset.Texture original,
		@Share("frozenLib$newCapeTexture") LocalRef<ClientAsset.Texture> newCapeAssetRef
	) {
		final ClientAsset.Texture newCapeAsset = newCapeAssetRef.get();
		if (newCapeAsset != null) return newCapeAsset;
		return original;
	}

}
