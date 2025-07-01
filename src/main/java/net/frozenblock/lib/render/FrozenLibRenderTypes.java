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

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.Util;

@Environment(EnvType.CLIENT)
public final class FrozenLibRenderTypes {

    public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize(
		(texture, affectsOutline) -> create(
			FrozenLibConstants.string("entity_translucent_emissive_fixed"),
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			true,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setCullState(RenderStateShard.NO_CULL)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(affectsOutline)
		)
    );

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL = Util.memoize(
		(texture, affectsOutline) -> create(
			FrozenLibConstants.string("entity_translucent_emissive_fixed_cull"),
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			true,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setCullState(RenderStateShard.CULL)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(affectsOutline)
		)
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER = Util.memoize(
		(texture, affectsOutline) -> create(
			FrozenLibConstants.string("entity_translucent_emissive_always_render"),
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setCullState(RenderStateShard.NO_CULL)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
				.createCompositeState(affectsOutline)
		)
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL = Util.memoize(
		(texture, affectsOutline) -> create(
			FrozenLibConstants.string("entity_translucent_emissive_always_render_cull"),
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setCullState(RenderStateShard.CULL)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
				.createCompositeState(affectsOutline)
		)
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION_OUTER = Util.memoize(
		(texture, affectsOutline) -> create(
			FrozenLibConstants.string("apparition"),
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			1536,
			true,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(FrozenLibShaders.RENDERTYPE_APPARITION_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
				.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
				.setWriteMaskState(RenderType.COLOR_WRITE)
				.setCullState(RenderStateShard.CULL)
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(false)
		)
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize(
		(identifier, affectsOutline) -> {
			RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(identifier, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setCullState(RenderStateShard.CULL)
				.setWriteMaskState(RenderStateShard.COLOR_WRITE)
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(affectsOutline);
			return create(
				FrozenLibConstants.string("entity_translucent_emissive_cull"),
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				true,
				true,
				multiPhaseParameters
			);
		}
	);

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

	public static RenderType.CompositeRenderType create(
		String name,
		VertexFormat vertexFormat,
		VertexFormat.Mode drawMode,
		int expectedBufferSize,
		boolean hasCrumbling,
		boolean translucent,
		RenderType.CompositeState phases
    ) {
        return new RenderType.CompositeRenderType(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }
}
