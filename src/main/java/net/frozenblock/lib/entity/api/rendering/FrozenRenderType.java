/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.entity.api.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiFunction;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class FrozenRenderType {

    public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize(
            ((identifier, affectsOutline) -> {
                RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(identifier, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .createCompositeState(affectsOutline);
                return create(
                        "entity_translucent_emissive_fixed_frozenlib",
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        256,
                        true,
                        true,
                        multiPhaseParameters
                );
            })
    );

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER = Util.memoize(
			((texture, affectsOutline) -> create(
					"entity_translucent_emissive_always_render_frozenlib",
					DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
					VertexFormat.Mode.QUADS,
					256,
					false,
					true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
							.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
							.setCullState(RenderStateShard.NO_CULL)
							.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
							.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
							.setOverlayState(RenderStateShard.OVERLAY)
							.createCompositeState(affectsOutline)
			))
	);

    public static RenderType entityTranslucentEmissiveFixed(ResourceLocation resourceLocation) {
        return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(resourceLocation, true);
    }

	public static RenderType entityTranslucentEmissiveFixedNoOutline(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(resourceLocation, false);
	}

	public static RenderType entityTranslucentEmissiveAlwaysRender(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER.apply(resourceLocation, false);
	}

    /*@Nullable
    public static ShaderInstance renderTypeTranslucentCutoutShader;

    public static final RenderStateShard.ShaderStateShard RENDERTYPE_TRANSLUCENT_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(
            WilderWildClient::getRenderTypeTranslucentCutoutShader
    );

    @Nullable
    public static ShaderInstance getRenderTypeTranslucentCutoutShader() {
        return renderTypeTranslucentCutoutShader;
    }

    public static final RenderType TRANSLUCENT_CUTOUT = create(
            "translucent_cutout_wilderwild", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, RenderType.translucentState(RENDERTYPE_TRANSLUCENT_CUTOUT_SHADER)
    );

    public static RenderType translucentCutout() {
        return TRANSLUCENT_CUTOUT;
    }*/

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
