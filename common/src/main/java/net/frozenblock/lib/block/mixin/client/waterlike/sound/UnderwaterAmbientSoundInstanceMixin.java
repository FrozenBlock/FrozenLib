/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.client.waterlike.sound;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.api.waterlike.WaterLikeTypes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance.class)
public class UnderwaterAmbientSoundInstanceMixin {

	@Shadow
	@Final
	private LocalPlayer player;

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isUnderWater()Z"
		)
	)
	public boolean frozenLib$stopAdditionsIfInWaterLike(boolean original) {
		if (WaterLikeTypes.getPlayerRandomInside(this.player).isPresent()) return false;
		return original;
	}

}
