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

package net.frozenblock.lib.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.pipeline.variant.EntityRenderPipelineVariant;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
@ClientOnly
public final class FrozenLibEntityRenderPipelineVariants {
	// NO SHADING
	public static final EntityRenderPipelineVariant SOLID_NO_SHADING = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("solid_no_shading"),
			ColorTargetState.DEFAULT,
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.NONE,
			false,
			true,
			false,
			false,
			false,
			false,
			false,
			false,
			false
		)
	);

	public static final EntityRenderPipelineVariant CUTOUT_NO_SHADING = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("cutout_no_shading"),
			ColorTargetState.DEFAULT,
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.NONE,
			false,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			false
		)
	);

	public static final EntityRenderPipelineVariant TRANSLUCENT_NO_SHADING = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("translucent_no_shading"),
			new ColorTargetState(BlendFunction.TRANSLUCENT),
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.NONE,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			false
		)
	);

	// NO SHADING CULL
	public static final EntityRenderPipelineVariant SOLID_NO_SHADING_CULL = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("solid_no_shading_cull"),
			ColorTargetState.DEFAULT,
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.NONE,
			false,
			true,
			false,
			true,
			false,
			false,
			false,
			false,
			false
		)
	);

	public static final EntityRenderPipelineVariant CUTOUT_NO_SHADING_CULL = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("cutout_no_shading_cull"),
			ColorTargetState.DEFAULT,
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.NONE,
			false,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			false
		)
	);

	public static final EntityRenderPipelineVariant TRANSLUCENT_NO_SHADING_CULL = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("translucent_no_shading_cull"),
			new ColorTargetState(BlendFunction.TRANSLUCENT),
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.NONE,
			true,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			false
		)
	);

	// EMISSIVE
	public static final EntityRenderPipelineVariant TRANSLUCENT_EMISSIVE_FIXED = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("translucent_emissive_fixed"),
			new ColorTargetState(BlendFunction.TRANSLUCENT),
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.PER_FACE,
			true,
			true,
			true,
			false,
			false,
			true,
			false,
			false,
			false
		)
	);

	// EMISSIVE CULL
	public static final EntityRenderPipelineVariant TRANSLUCENT_EMISSIVE_CULL = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("translucent_emissive_cull"),
			new ColorTargetState(BlendFunction.TRANSLUCENT),
			new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false),
			EntityRenderPipelineVariant.Lighting.PER_FACE,
			true,
			true,
			true,
			true,
			false,
			true,
			false,
			false,
			false
		)
	);

	public static final EntityRenderPipelineVariant TRANSLUCENT_EMISSIVE_FIXED_CULL = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("translucent_emissive_fixed_cull"),
			new ColorTargetState(BlendFunction.TRANSLUCENT),
			DepthStencilState.DEFAULT,
			EntityRenderPipelineVariant.Lighting.PER_FACE,
			true,
			true,
			true,
			true,
			false,
			true,
			false,
			false,
			false
		)
	);

	// APPARITION
	public static final EntityRenderPipelineVariant APPARITION_OUTER = EntityRenderPipelineVariant.VALUES.add(
		new EntityRenderPipelineVariant(
			FrozenLibConstants.safeString("apparition_outer"),
			new ColorTargetState(BlendFunction.TRANSLUCENT),
			new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false),
			EntityRenderPipelineVariant.Lighting.NONE,
			true,
			true,
			true,
			true,
			false,
			true,
			false,
			false,
			false
		)
	);

	public static void init() {}
}
