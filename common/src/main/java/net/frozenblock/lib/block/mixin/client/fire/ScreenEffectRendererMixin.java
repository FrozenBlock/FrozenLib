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
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(
		method = "submit",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/model/sprite/SpriteGetter;get(Lnet/minecraft/client/resources/model/sprite/SpriteId;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
		)
	)
	public TextureAtlasSprite frozenLib$submitFireType(
		SpriteGetter instance, SpriteId spriteId, Operation<TextureAtlasSprite> original
	) {
		final FireData fireData = FireData.ATTACHMENT.get(this.minecraft.player);
		if (fireData == null) return original.call(instance, spriteId);

		final Optional<Identifier> optionalTexture = fireData.type().value().textures().texture1();
		if (optionalTexture.isPresent()) spriteId = Sheets.BLOCKS_MAPPER.apply(optionalTexture.get());

		return original.call(instance, spriteId);
	}
}
