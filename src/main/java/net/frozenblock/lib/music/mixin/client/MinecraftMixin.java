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

package net.frozenblock.lib.music.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.music.api.client.StructureMusicApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	@Nullable
	public LocalPlayer player;

	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/sounds/Music;)Lnet/minecraft/client/sounds/MusicInfo;",
			ordinal = 2
		)
	)
	public MusicInfo frozenLib$getEndStructureMusicInfo(MusicInfo original) {
		return StructureMusicApi.chooseMusicInfoOrStructureMusicInfo(this.player, original);
	}

	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/sounds/Music;F)Lnet/minecraft/client/sounds/MusicInfo;"
		),
		slice = @Slice(
			from = @At(
				value = "NEW",
				target = "(Lnet/minecraft/sounds/Music;F)Lnet/minecraft/client/sounds/MusicInfo;",
				shift = At.Shift.AFTER,
				ordinal = 0
			)
		)
	)
	public MusicInfo frozenLib$getGameStructureMusicInfo(MusicInfo original) {
		return StructureMusicApi.chooseMusicInfoOrStructureMusicInfo(this.player, original);
	}

}
