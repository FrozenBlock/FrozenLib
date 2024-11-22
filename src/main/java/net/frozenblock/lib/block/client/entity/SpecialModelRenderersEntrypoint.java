package net.frozenblock.lib.block.client.entity;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

/**
 * Use `frozenlib:special_model_renderers` to access this.
 */
@Environment(EnvType.CLIENT)
public interface SpecialModelRenderersEntrypoint {

	/**
	 * Runs when {@link SpecialModelRenderers#bootstrap()} is called.
	 */
	void registerSpecialModelRenderers(ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> mapper);
}
