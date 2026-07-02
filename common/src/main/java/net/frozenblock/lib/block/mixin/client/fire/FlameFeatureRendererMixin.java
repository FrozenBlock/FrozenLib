/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.client.fire;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.renderer.FrozenLibRenderState;import net.frozenblock.lib.renderer.FrozenLibRenderStateDataKeys;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(FlameFeatureRenderer.class)
public class FlameFeatureRendererMixin {

	@WrapOperation(
		method = "buildGroup",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer;prepare(Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer$Submit;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"
		)
	)
	public void frozenLib$submitFireType(
		FlameFeatureRenderer instance, FlameFeatureRenderer.Submit submit, VertexConsumer buffer, TextureAtlasSprite fire1, TextureAtlasSprite fire2, Operation<Void> original,
		FeatureFrameContext context
	) {
		final EntityRenderState entityRenderState = submit.entityRenderState();
		final FireType fireType = ((FrozenLibRenderState) entityRenderState).frozenLib$getData(FrozenLibRenderStateDataKeys.FIRE_TYPE);
		if (fireType != null) {
			final AtlasManager atlasManager = context.atlasManager();
			final FireType.TextureSettings textures = fireType.textures();
			if (textures.texture0().isPresent()) fire1 = atlasManager.get(Sheets.BLOCKS_MAPPER.apply(textures.texture0().get()));
			if (textures.texture1().isPresent()) fire2 = atlasManager.get(Sheets.BLOCKS_MAPPER.apply(textures.texture1().get()));
		}

		original.call(instance, submit, buffer, fire1, fire2);
	}
}
