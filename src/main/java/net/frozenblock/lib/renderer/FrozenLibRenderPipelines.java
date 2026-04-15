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

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;

@Environment(EnvType.CLIENT)
public final class FrozenLibRenderPipelines {
	public static final RenderPipeline ENTITY_CUTOUT_NO_SHADING = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_cutout_no_shading"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline ENTITY_CUTOUT_NO_SHADING_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_cutout_no_shading_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_NO_SHADING_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_no_shading_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(true)
			.build()
	);

	// TODO: check if this functions as intended (depth stencil)
	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_FIXED = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_fixed"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("PER_FACE_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(false)
			.withDepthStencilState(DepthStencilState.DEFAULT)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("PER_FACE_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(true)
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_FIXED_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_fixed_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("PER_FACE_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(true)
			.withDepthStencilState(DepthStencilState.DEFAULT)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_always_render"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("PER_FACE_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			// TODO: test
			.withDepthStencilState(Optional.empty())
			.withCull(false)
			.build()
	);

	public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/entity_translucent_emissive_always_render_cull"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("PER_FACE_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			// TODO: test
			.withDepthStencilState(Optional.empty())
			.withCull(true)
			.build()
	);

	// TODO: test
	public static final RenderPipeline APPARITION_OUTER = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(FrozenLibConstants.id("pipeline/apparition_outer"))
			.withShaderDefine("ALPHA_CUTOUT", 0.1F)
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(true)
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
			.build()
	);

}
