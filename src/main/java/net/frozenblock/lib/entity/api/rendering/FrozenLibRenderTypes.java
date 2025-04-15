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
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

@Environment(EnvType.CLIENT)
public final class FrozenLibRenderTypes {

    public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize(
		(resourceLocation, boolean_) -> {
		RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false))
			.setOverlayState(RenderStateShard.OVERLAY)
			.createCompositeState(boolean_);
		return RenderType.create(
			FrozenLibConstants.safeString("entity_translucent_emissive_fixed"),
			1536,
			true,
			true,
			FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_FIXED,
			compositeState
		);
	});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false))
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.safeString("entity_translucent_emissive_cull"),
				1536,
				true,
				true,
				FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_CULL,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false))
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.safeString("entity_translucent_emissive_always_render"),
				1536,
				true,
				true,
				FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false))
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.safeString("entity_translucent_emissive_always_render_cull"),
				1536,
				true,
				true,
				FrozenLibRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE_ALWAYS_RENDER_CULL,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false))
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.safeString("apparition"),
				1536,
				false,
				true,
				FrozenLibRenderPipelines.APPARITION,
				compositeState
			);
		});

	public static final BiFunction<ResourceLocation, Boolean, RenderType> APPARITION_CULL = Util.memoize(
		(resourceLocation, boolean_) -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false))
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(boolean_);
			return RenderType.create(
				FrozenLibConstants.safeString("apparition_cull"),
				1536,
				false,
				true,
				FrozenLibRenderPipelines.APPARITION_CULL,
				compositeState
			);
		});

    public static RenderType entityTranslucentEmissiveFixed(ResourceLocation resourceLocation) {
        return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(resourceLocation, true);
    }

	public static RenderType entityTranslucentEmissiveCull(ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_EMISSIVE_CULL.apply(resourceLocation, true);
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

	public static RenderType apparition(ResourceLocation resourceLocation) {
		return APPARITION.apply(resourceLocation, false);
	}

	public static RenderType apparitionCull(ResourceLocation resourceLocation) {
		return APPARITION_CULL.apply(resourceLocation, false);
	}
}
