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

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.renderer.RenderPipelines;

@Environment(EnvType.CLIENT)
public final class FrozenLibRenderPipelines {
	public static final RenderPipeline ENTITY_CUTOUT_NO_SHADING = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_cutout_no_shading"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withSampler("Sampler1")
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_FIXED = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_fixed"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("PER_FACE_LIGHTING")
			.withSampler("Sampler1")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(false)
			.withDepthWrite(true)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("PER_FACE_LIGHTING")
			.withSampler("Sampler1")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(true)
			.withDepthWrite(false)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_fixed_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("PER_FACE_LIGHTING")
			.withSampler("Sampler1")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(true)
			.withDepthWrite(true)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_always_render"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("PER_FACE_LIGHTING")
			.withSampler("Sampler1")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withCull(false)
			.withDepthWrite(false)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_always_render_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("PER_FACE_LIGHTING")
			.withSampler("Sampler1")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withCull(true)
			.withDepthWrite(false)
			.build()
	);

	public static final RenderPipeline APPARITION_OUTER = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/apparition_outer"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withSampler("Sampler1")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(true)
			.withDepthWrite(false)
			.build()
	);

}
