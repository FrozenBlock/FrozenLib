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
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

@UtilityClass
@ClientOnly
public final class FrozenLibRenderTypes {
	// ENTITY NO SHADING
	public static final Function<Identifier, RenderType> ENTITY_NO_SHADING = Util.memoize(texture -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_NO_SHADING)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_no_shading"), state);
	});

	public static RenderType entityNoShading(Identifier texture) {
		return ENTITY_NO_SHADING.apply(texture);
	}

	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT_NO_SHADING = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_CUTOUT_NO_SHADING)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_cutout_no_shading"), state);
	});

	public static RenderType entityCutoutNoShading(Identifier texture, boolean affectsOutline) {
		return ENTITY_CUTOUT_NO_SHADING.apply(texture, affectsOutline);
	}

	public static RenderType entityCutoutNoShading(Identifier texture) {
		return entityCutoutNoShading(texture, true);
	}

	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_NO_SHADING = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_NO_SHADING)
			.setOitPipelines(FrozenLibRenderPipelines.OIT_ENTITY_NO_SHADING)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_no_shading"), state);
	});

	public static RenderType entityTranslucentNoShading(Identifier texture, boolean affectsOutline) {
		return ENTITY_TRANSLUCENT_NO_SHADING.apply(texture, affectsOutline);
	}

	public static RenderType entityTranslucentNoShading(Identifier texture) {
		return entityTranslucentNoShading(texture, true);
	}

	// ENTITY NO SHADING CULL
	public static final Function<Identifier, RenderType> ENTITY_NO_SHADING_CULL = Util.memoize(texture -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_NO_SHADING_CULL)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_no_shading_cull"), state);
	});

	public static RenderType entityNoShadingCull(Identifier texture) {
		return ENTITY_NO_SHADING_CULL.apply(texture);
	}

	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT_NO_SHADING_CULL = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_CUTOUT_NO_SHADING_CULL)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_cutout_no_shading_cull"), state);
	});

	public static RenderType entityCutoutNoShadingCull(Identifier texture, boolean affectsOutline) {
		return ENTITY_CUTOUT_NO_SHADING_CULL.apply(texture, affectsOutline);
	}

	public static RenderType entityCutoutNoShadingCull(Identifier texture) {
		return entityCutoutNoShadingCull(texture, true);
	}


	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_NO_SHADING_CULL = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_NO_SHADING_CULL)
			.setOitPipelines(FrozenLibRenderPipelines.OIT_ENTITY_NO_SHADING_CULL)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_no_shading_cull"), state);
	});

	public static RenderType entityTranslucentNoShadingCull(Identifier texture, boolean affectsOutline) {
		return ENTITY_TRANSLUCENT_NO_SHADING_CULL.apply(texture, affectsOutline);
	}

	public static RenderType entityTranslucentNoShadingCull(Identifier texture) {
		return entityTranslucentNoShadingCull(texture, true);
	}

	// ENTITY EMISSIVE
	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_FIXED)
			.setOitPipelines(RenderPipelines.OIT_ENTITY_EMISSIVE)
			.withTexture("Sampler0", texture)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_fixed"), state);
	});

	public static RenderType entityTranslucentEmissiveFixed(Identifier texture, boolean affectsOutline) {
		return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(texture, affectsOutline);
	}

	public static RenderType entityTranslucentEmissiveFixed(Identifier texture) {
		return entityTranslucentEmissiveFixed(texture, true);
	}

	// ENTITY EMISSIVE CULL
	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_CULL)
			.setOitPipelines(FrozenLibRenderPipelines.OIT_ENTITY_EMISSIVE_CULL)
			.withTexture("Sampler0", texture)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_cull"), state);
	});

	public static RenderType entityTranslucentEmissiveCull(Identifier texture, boolean affectsOutline) {
		return ENTITY_TRANSLUCENT_EMISSIVE_CULL.apply(texture, affectsOutline);
	}

	public static RenderType entityTranslucentEmissiveCull(Identifier texture) {
		return entityTranslucentEmissiveCull(texture, true);
	}

	public static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL)
			.setOitPipelines(FrozenLibRenderPipelines.OIT_ENTITY_EMISSIVE_CULL)
			.withTexture("Sampler0", texture)
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("entity_translucent_emissive_fixed_cull"), state);
	});

	public static RenderType entityTranslucentEmissiveFixedCull(Identifier texture, boolean affectsOutline) {
		return ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL.apply(texture, affectsOutline);
	}

	public static RenderType entityTranslucentEmissiveFixedCull(Identifier texture) {
		return entityTranslucentEmissiveFixedCull(texture, true);
	}

	// APPARITION
	public static final BiFunction<Identifier, Boolean, RenderType> APPARITION_OUTER = Util.memoize((texture, affectsOutline) -> {
		final RenderSetup state = RenderSetup.builder(FrozenLibRenderPipelines.APPARITION_OUTER)
			.setOitPipelines(FrozenLibRenderPipelines.OIT_APPARITION_OUTER)
			.withTexture("Sampler0", texture)
			.useOverlay()
			.sortOnUpload()
			.setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
			.createRenderSetup();
		return RenderType.create(FrozenLibConstants.safeString("apparition_outer"), state);
	});

	public static RenderType apparitionOuter(Identifier texture, boolean affectsOutline) {
		return APPARITION_OUTER.apply(texture, affectsOutline);
	}

	public static RenderType apparitionOuter(Identifier texture) {
		return apparitionOuter(texture, true);
	}

	// BLOCK SHEET NO SHADING
	public static final RenderType BLOCK_SHEET_CUTOUT_NO_SHADING = ENTITY_CUTOUT_NO_SHADING.apply(TextureAtlas.LOCATION_BLOCKS, true);
	public static final RenderType BLOCK_SHEET_TRANSLUCENT_NO_SHADING = ENTITY_TRANSLUCENT_NO_SHADING.apply(TextureAtlas.LOCATION_BLOCKS, true);

	// BLOCK SHEET NO SHADING CULL
	public static final RenderType BLOCK_SHEET_CUTOUT_NO_SHADING_CULL = ENTITY_CUTOUT_NO_SHADING_CULL.apply(TextureAtlas.LOCATION_BLOCKS, true);
	public static final RenderType BLOCK_SHEET_TRANSLUCENT_NO_SHADING_CULL = ENTITY_TRANSLUCENT_NO_SHADING_CULL.apply(TextureAtlas.LOCATION_BLOCKS, true);
}
