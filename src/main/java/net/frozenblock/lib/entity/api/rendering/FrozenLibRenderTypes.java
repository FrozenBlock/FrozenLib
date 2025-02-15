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

import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

@Environment(EnvType.CLIENT)
public final class FrozenLibRenderTypes {

	// TODO: Look into RenderType, see how these are now handled. Use EndermanRenderer for reference with Eye rendering.
    public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
		(resourceLocation, boolean_) -> {
		RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setTextureState(
			new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false)
		).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(boolean_);
		return RenderType.create(
			FrozenLibConstants.string("entity_translucent_emissive"),
			1536,
			true,
			true,
			RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE,
			compositeState
		);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setTextureState(
				new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false)
			).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.string("entity_translucent_emissive_cull"),
				1536,
				true,
				true,
				FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_CULL,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setTextureState(
				new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false)
			).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.string("entity_translucent_emissive_always_render"),
				1536,
				true,
				true,
				FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setTextureState(
				new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false)
			).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.string("entity_translucent_emissive_always_render_cull"),
				1536,
				true,
				true,
				FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION_OUTER = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setTextureState(
				new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false)
			).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(false);
			return RenderType.create(
				FrozenLibConstants.string("apparition_outer"),
				1536,
				false,
				true,
				FrozenLibRenderPipelines.APPARITION_OUTER,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION_OUTER_CULL = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setTextureState(
				new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false)
			).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(false);
			return RenderType.create(
				FrozenLibConstants.string("apparition_outer_cull"),
				1536,
				false,
				true,
				FrozenLibRenderPipelines.APPARITION_OUTER_CULL,
				compositeState
			);
		});

    public static RenderType entityTranslucentEmissive(ResourceLocation resourceLocation) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(resourceLocation, true);
    }

	public static RenderType entityTranslucentEmissiveCull(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_CULL.apply(resourceLocation, true);
	}

	public static RenderType entityTranslucentEmissiveNoOutline(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE.apply(resourceLocation, false);
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
}
