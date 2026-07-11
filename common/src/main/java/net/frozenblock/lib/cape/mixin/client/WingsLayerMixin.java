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

package net.frozenblock.lib.cape.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
@Mixin(WingsLayer.class)
public class WingsLayerMixin {

	@Inject(
		method = "getPlayerElytraTexture",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;skin:Lnet/minecraft/world/entity/player/PlayerSkin;",
			opcode = Opcodes.GETFIELD
		),
		cancellable = true
	)
	private static void frozenLib$useFrozenLibCapeAsElytra(
		CallbackInfoReturnable<Identifier> info,
		@Local(name = "playerState") AvatarRenderState playerState
	) {
		if (!playerState.showCape) return;

		final ClientAsset.Texture newCapeAsset = playerState.frozenLib$getData(ClientCapeUtil.CAPE_TEXTURE_DATA_KEY);
		if (newCapeAsset != null) info.setReturnValue(newCapeAsset.texturePath());
	}
}
