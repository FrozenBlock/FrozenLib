package net.frozenblock.lib.levelgen.attribute.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.levelgen.attribute.api.FrozenLibEnvironmentAttributes;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
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
