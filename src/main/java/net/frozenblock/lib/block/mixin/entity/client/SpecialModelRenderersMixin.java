package net.frozenblock.lib.block.mixin.entity.client;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.block.client.entity.SpecialModelRenderersEntrypoint;
import net.frozenblock.lib.block.client.entity.SpecialModelRenderersEvents;
import net.frozenblock.lib.item.impl.sherd.DecoratedPotPatternRegistryEntrypoint;
import net.frozenblock.lib.mobcategory.api.entrypoint.FrozenMobCategoryEntrypoint;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SpecialModelRenderers.class)
public class SpecialModelRenderersMixin {

	@Shadow
	@Final
	private static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> ID_MAPPER;

	@WrapOperation(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;",
			ordinal = 0
		)
	)
	private static ImmutableMap.Builder frozenLib$put(ImmutableMap.Builder instance, Object key, Object value, Operation<ImmutableMap.Builder> original) {
		SpecialModelRenderersEvents.MAP_INIT.invoker().onMapInit(instance);
		return original.call(instance, key, value);
	}

	@Inject(method = "bootstrap", at = @At("TAIL"))
	private static void frozenLib$bootstrap(CallbackInfo info) {
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:special_model_renderers", SpecialModelRenderersEntrypoint.class).forEach(entrypoint -> {
			try {
				SpecialModelRenderersEntrypoint specialModelRenderersEntrypoint = entrypoint.getEntrypoint();
				specialModelRenderersEntrypoint.registerSpecialModelRenderers(ID_MAPPER);
			} catch (Throwable ignored) {
			}
		});
	}

}
