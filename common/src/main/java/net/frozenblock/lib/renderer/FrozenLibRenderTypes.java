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

package net.frozenblock.lib.renderer;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.frozenblock.lib.FrozenLibConstants;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

@ClientOnly
public final class FrozenLibRenderTypes {
	// TODO: test if these work
	public static final Function<Identifier, RenderType> ENTITY_CUTOUT_NO_SHADING = Util.memoize(identifier -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_CUTOUT_NO_SHADING)
			.withTexture("Sampler0", identifier)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_cutout_no_lightmap"), renderSetup);
	});

	public static final Function<Identifier, RenderType> ENTITY_CUTOUT_NO_SHADING_CULL = Util.memoize(identifier -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_CUTOUT_NO_SHADING_CULL)
			.withTexture("Sampler0", identifier)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_cutout_no_lightmap_cull"), renderSetup);
	});

	public static final Function<Identifier, RenderType> ENTITY_TRANSLUCENT_NO_SHADING_CULL = Util.memoize(identifier -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_NO_SHADING_CULL)
			.setOitPipelines(RenderPipelines.OIT_ENTITY)
			.withTexture("Sampler0", identifier)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_no_lightmap_cull"), renderSetup);
	});

	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize((identifier, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_FIXED)
			.setOitPipelines(RenderPipelines.OIT_ENTITY_EMISSIVE)
			.withTexture("Sampler0", identifier)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_fixed"), renderSetup);
	});

	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize((identifier, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_CULL)
			.setOitPipelines(RenderPipelines.OIT_ENTITY_EMISSIVE)
			.withTexture("Sampler0", identifier)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_cull"), renderSetup);
	});

	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL = Util.memoize((identifier, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL)
			.setOitPipelines(RenderPipelines.OIT_ENTITY_EMISSIVE)
			.withTexture("Sampler0", identifier)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_fixed_cull"), renderSetup);
	});

	public static final BiFunction<Identifier, Boolean, RenderType> APPARITION_OUTER = Util.memoize((identifier, affectsOutline) -> {
		final RenderSetup renderSetup = RenderSetup.builder(FrozenLibRenderPipelines.APPARITION_OUTER)
			.setOitPipelines(RenderPipelines.OIT_ENTITY)
			.withTexture("Sampler0", identifier)
			.useOverlay()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("apparition_outer"), renderSetup);
	});

	public static final RenderType NO_SHADING_CUTOUT_BLOCK_SHEET = ENTITY_CUTOUT_NO_SHADING_CULL.apply(TextureAtlas.LOCATION_BLOCKS);
	public static final RenderType NO_SHADING_TRANSLUCENT_BLOCK_SHEET = ENTITY_TRANSLUCENT_NO_SHADING_CULL.apply(TextureAtlas.LOCATION_BLOCKS);

	public static RenderType entityCutoutNoShading(Identifier identifier) {
		return ENTITY_CUTOUT_NO_SHADING.apply(identifier);
	}

	public static RenderType entityCutoutNoShadingCull(Identifier identifier) {
		return ENTITY_CUTOUT_NO_SHADING_CULL.apply(identifier);
	}

	public static RenderType entityTranslucentNoShadingCull(Identifier identifier) {
		return ENTITY_TRANSLUCENT_NO_SHADING_CULL.apply(identifier);
	}

    public static RenderType entityTranslucentEmissiveFixed(Identifier identifier) {
        return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(identifier, true);
    }

	public static RenderType entityTranslucentEmissiveFixedCull(Identifier identifier) {
		return ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL.apply(identifier, true);
	}

	public static RenderType entityTranslucentEmissiveFixedNoOutline(Identifier identifier) {
		return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(identifier, false);
	}

	public static RenderType apparitionOuter(Identifier identifier) {
		return APPARITION_OUTER.apply(identifier, false);
	}

	public static RenderType entityTranslucentEmissiveCull(Identifier identifier) {
		return ENTITY_TRANSLUCENT_EMISSIVE_CULL.apply(identifier, true);
	}
}
