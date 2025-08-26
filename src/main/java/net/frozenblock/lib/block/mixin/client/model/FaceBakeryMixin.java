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

package net.frozenblock.lib.block.mixin.client.model;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.resources.metadata.emissive.EmissiveMetadataSection;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(FaceBakery.class)
public class FaceBakeryMixin {

	@Inject(method = "bakeQuad", at = @At("HEAD"))
	private static void frozenLib$bakeWithEmission(
		CallbackInfoReturnable<BakedQuad> info,
		@Local(argsOnly = true) TextureAtlasSprite sprite,
		@Local(argsOnly = true) LocalBooleanRef shade,
		@Local(argsOnly = true) LocalIntRef lightEmission
	) {
		SpriteContents contents = sprite.contents();

		Optional<EmissiveMetadataSection> optionalEmissiveMetadata = contents.getAdditionalMetadata(EmissiveMetadataSection.TYPE);
		if (optionalEmissiveMetadata.isPresent()) {
			EmissiveMetadataSection emissiveMetadata = optionalEmissiveMetadata.get();
			shade.set(emissiveMetadata.shade().orElse(shade.get()));
			lightEmission.set(emissiveMetadata.lightEmission());
		} else {
			lightEmission.set(contents.name().getPath().endsWith("_frozenlib_emissive") ? 15 : lightEmission.get());
		}
	}

}
