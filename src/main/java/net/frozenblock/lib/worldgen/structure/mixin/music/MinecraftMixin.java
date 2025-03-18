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

package net.frozenblock.lib.worldgen.structure.mixin.music;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.worldgen.structure.api.music.client.StructureMusicApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	@Nullable
	public LocalPlayer player;

	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/sounds/Musics;END:Lnet/minecraft/sounds/Music;"
		)
	)
	public Music frozenLib$getEndStructureMusic(Music original) {
		return StructureMusicApi.chooseMusicOrStructureMusic(this.player, original);
	}

	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"
		)
	)
	public Object frozenLib$getGameStructureMusic(Object original) {
		// This is just an extra check because of Java type erasure.
		if (!(original instanceof Music)) return original;
		return StructureMusicApi.chooseMusicOrStructureMusic(this.player, (Music) original);
	}

	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/sounds/Musics;UNDER_WATER:Lnet/minecraft/sounds/Music;",
			ordinal = 1
		)
	)
	public Music frozenLib$getUnderWaterStructureMusic(Music original) {
		return StructureMusicApi.chooseMusicOrStructureMusic(this.player, original);
	}

}
