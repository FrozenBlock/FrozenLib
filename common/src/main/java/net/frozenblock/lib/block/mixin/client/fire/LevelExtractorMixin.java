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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.renderer.FrozenLibRenderStateDataKeys;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(LevelExtractor.class)
public class LevelExtractorMixin {

	@ModifyExpressionValue(
		method = "extractPlayerState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isOnFire()Z"
		)
	)
	public boolean frozenLib$extractFirstPersonFireType(
		boolean original,
		Camera camera, DeltaTracker deltaTracker, float worldPartialTicks, PlayerRenderState state,
		@Local(name = "player") LocalPlayer player
	) {
		final FireData fireData = FireData.ATTACHMENT.get(player);
		if (fireData == null) return original;

		state.frozenLib$setData(FrozenLibRenderStateDataKeys.FIRE_TYPE, fireData.type().value());

		return original;
	}
}
