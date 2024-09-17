package net.frozenblock.lib.cape.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.cape.client.impl.AbstractClientPlayerCapeInterface;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(CapeLayer.class)
public class CapeLayerMixin {

	@ModifyExpressionValue(
		method = "render*",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/PlayerSkin;capeTexture()Lnet/minecraft/resources/ResourceLocation;",
			ordinal = 0
		)
	)
	public ResourceLocation frozenLib$captureAndChangeCapeLocation(
		ResourceLocation resourceLocation,
		PoseStack matrices, MultiBufferSource vertexConsumers, int i, AbstractClientPlayer abstractClientPlayer,
		@Share("frozenLib$newCapeTexture") LocalRef<ResourceLocation> newCapeTexture
	) {
		if (abstractClientPlayer instanceof AbstractClientPlayerCapeInterface capeInterface) {
			ResourceLocation capeTexture = capeInterface.frozenLib$getCape();
			if (capeTexture != null) {
				newCapeTexture.set(capeTexture);
				return capeTexture;
			}
		}
		return resourceLocation;
	}

	@ModifyExpressionValue(
		method = "render*",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/PlayerSkin;capeTexture()Lnet/minecraft/resources/ResourceLocation;",
			ordinal = 1
		)
	)
	public ResourceLocation frozenLib$renderNewCape(
		ResourceLocation resourceLocation,
		@Share("frozenLib$newCapeTexture") LocalRef<ResourceLocation> newCapeTexture
	) {
		ResourceLocation capeTexture = newCapeTexture.get();
		if (capeTexture != null) return capeTexture;
		return resourceLocation;
	}

}
