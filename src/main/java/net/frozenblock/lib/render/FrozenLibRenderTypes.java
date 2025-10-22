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

package net.frozenblock.lib.render;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;

@Environment(EnvType.CLIENT)
public final class FrozenLibRenderTypes {

	public static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT_NO_SHADING = Util.memoize(resourceLocation -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_CUTOUT_NO_SHADING)
			.withTexture("Sampler0", resourceLocation)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_cutout_no_lightmap"), renderSetup);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize((resourceLocation, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_FIXED)
			.withTexture("Sampler0", resourceLocation)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_fixed"), renderSetup);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize((resourceLocation, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_CULL)
			.withTexture("Sampler0", resourceLocation)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_cull"), renderSetup);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL = Util.memoize((resourceLocation, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL)
			.withTexture("Sampler0", resourceLocation)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_fixed_cull"), renderSetup);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER = Util.memoize((resourceLocation, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER)
			.withTexture("Sampler0", resourceLocation)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_always_render"), renderSetup);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL = Util.memoize((resourceLocation, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL)
			.withTexture("Sampler0", resourceLocation)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_always_render_cull"), renderSetup);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION_OUTER = Util.memoize((resourceLocation, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.APPARITION_OUTER)
			.withTexture("Sampler0", resourceLocation)
			.useOverlay()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("apparition_outer"), renderSetup);
	});

	public static RenderType entityCutoutNoShading(ResourceLocation resourceLocation) {
		return ENTITY_CUTOUT_NO_SHADING.apply(resourceLocation);
	}

    public static RenderType entityTranslucentEmissiveFixed(ResourceLocation resourceLocation) {
        return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(resourceLocation, true);
    }

	public static RenderType entityTranslucentEmissiveFixedCull(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL.apply(resourceLocation, true);
	}

	public static RenderType entityTranslucentEmissiveFixedNoOutline(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(resourceLocation, false);
	}

	public static RenderType entityTranslucentEmissiveAlwaysRender(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER.apply(resourceLocation, false);
	}

	public static RenderType entityTranslucentEmissiveAlwaysRenderCull(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL.apply(resourceLocation, false);
	}

	public static RenderType apparitionOuter(ResourceLocation resourceLocation) {
		return APPARITION_OUTER.apply(resourceLocation, false);
	}

	public static RenderType entityTranslucentEmissiveCull(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_CULL.apply(resourceLocation, true);
	}
}
