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

package net.frozenblock.lib.entity.api.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;

@Environment(EnvType.CLIENT)
public final class FrozenLibRenderTypes {

    public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize(
            ((identifier, affectsOutline) -> {
                RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(identifier, TriState.FALSE, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .createCompositeState(affectsOutline);
                return create(
					FrozenLibConstants.string("entity_translucent_emissive_fixed"),
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        256,
                        true,
                        true,
                        multiPhaseParameters
                );
            })
    );

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL = Util.memoize(
		((identifier, affectsOutline) -> {
			RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(identifier, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setCullState(RenderStateShard.CULL)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(affectsOutline);
			return create(
				FrozenLibConstants.string("entity_translucent_emissive_fixed_cull"),
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
							//.setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
							//.setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
							.createCompositeState(affectsOutline)
			))
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL = Util.memoize(
		((texture, affectsOutline) -> create(
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
				//.setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
				//.setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
				.createCompositeState(affectsOutline)
		))
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION_OUTER = Util.memoize(
		((texture, affectsOutline) -> create(
			FrozenLibConstants.string("apparition_outer"),
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderType.RENDERTYPE_EYES_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
				.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
				.setWriteMaskState(RenderType.COLOR_WRITE)
				.setCullState(RenderStateShard.NO_CULL)
				.createCompositeState(false)
		))
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION_OUTER_CULL = Util.memoize(
		((texture, affectsOutline) -> create(
			FrozenLibConstants.string("apparition_outer_cull"),
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderType.RENDERTYPE_EYES_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
				.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
				.setWriteMaskState(RenderType.COLOR_WRITE)
				.createCompositeState(false)
		))
	);

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize(
		((identifier, affectsOutline) -> {
			RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(identifier, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setCullState(RenderStateShard.CULL)
				.setWriteMaskState(RenderStateShard.COLOR_WRITE)
				.setOverlayState(RenderStateShard.OVERLAY)
				.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
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
		})
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

	public static RenderType apparitionOuterCull(ResourceLocation resourceLocation) {
		return APPARITION_OUTER_CULL.apply(resourceLocation, false);
	}

	public static RenderType entityTranslucentEmissiveCull(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_CULL.apply(resourceLocation, true);
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

	public static RenderType dynamicLightStencil() {
		return DYNAMIC_LIGHT_STENCIL;
	}

	public static RenderType dynamicLightColor() {
		return DYNAMIC_LIGHT_COLOR;
	}

	public static final RenderStateShard.OutputStateShard STENCIL_SETUP_AND_LEAK = new RenderStateShard.OutputStateShard("stencil_setup_and_leak", () -> {
		GL11C.glClear(GL11C.GL_STENCIL_BUFFER_BIT);
		GL11C.glColorMask(false, false, false, false);
		GL11C.glEnable(GL11C.GL_DEPTH_TEST);
		GL11C.glDepthMask(true);
		GL11C.glEnable(GL11C.GL_STENCIL_TEST);
		GL11C.glDepthMask(false);
		GL11C.glEnable(34383);
		GL11C.glDisable(2884);
		GL11C.glStencilFunc(519, 0, 255);
		GL20C.glStencilOpSeparate(1029, 7680, 34055, 7680);
		GL20C.glStencilOpSeparate(1028, 7680, 34056, 7680);
	}, () -> {
		GL11C.glDisable(34383);
		GL11C.glEnable(2884);
		GL11C.glColorMask(true, true, true, true);
	});

	public static final RenderStateShard.OutputStateShard STENCIL_RENDER_AND_CLEAR = new RenderStateShard.OutputStateShard("stencil_render_and_clear", () -> {
		GL11C.glStencilFunc(514, 1, 255);
		GL11C.glDepthFunc(516);
		GL11C.glCullFace(1028);
		GL20C.glStencilOpSeparate(1028, 7680, 7680, 7680);
	}, () -> {
		GL11C.glDepthFunc(515);
		GL11C.glCullFace(1029);
		GL11C.glDepthMask(true);
		GL11C.glDisable(2960);
	});

	private static final RenderType DYNAMIC_LIGHT_STENCIL = RenderType.create("dynamic_light_stencil", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_SHADER).setOutputState(STENCIL_SETUP_AND_LEAK).createCompositeState(false));

	private static final RenderType DYNAMIC_LIGHT_COLOR = RenderType.create("dynamic_light_color", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_SHADER).setOutputState(STENCIL_RENDER_AND_CLEAR).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(false));


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
