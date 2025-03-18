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
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.music.api.client.pitch.MusicPitchApi;
import net.frozenblock.lib.music.api.client.structure.StructureMusicApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
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

	@Shadow
	@Nullable
	public ClientLevel level;

	@WrapOperation(
		method = "getSituationalMusic",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;level()Lnet/minecraft/world/level/Level;",
			ordinal = 0
		)
	)
	public Level frozenLib$earlyBiomeCheck(
		LocalPlayer instance, Operation<Level> original,
		@Share("frozenLib$biomeHolder") LocalRef<Holder<Biome>> biomeHolderRef
	) {
		Level level = original.call(instance);
		Holder<Biome> biomeHolder = level.getBiome(this.player.blockPosition());
		biomeHolderRef.set(biomeHolder);
		return level;
	}

	@WrapOperation(
		method = "getSituationalMusic",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
		)
	)
	public Holder<Biome> frozenLib$useEarlierBiomeCheck(
		Level instance, BlockPos pos, Operation<Holder<Biome>> original,
		@Share("frozenLib$biomeHolder") LocalRef<Holder<Biome>> biomeHolderRef
	) {
		Holder<Biome> biomeHolder = biomeHolderRef.get();
		if (biomeHolder != null) return biomeHolder;
		return original.call(instance, pos);
	}

	@ModifyReturnValue(method = "getSituationalMusic", at = @At(value = "RETURN", ordinal = 0))
	public MusicInfo frozenLib$resetPitchIfPlayingScreenMusic(MusicInfo original) {
		MusicPitchApi.resetCurrentPitch();
		return original;
	}

	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;shouldPlayMusic()Z"
		)
	)
	public boolean frozenLib$resetPitchIfFightingEndBoss(boolean original) {
		if (original) MusicPitchApi.resetCurrentPitch();
		return original;
	}

	@ModifyReturnValue(method = "getSituationalMusic", at = @At(value = "RETURN", ordinal = 2))
	public MusicInfo frozenLib$resetPitchIfCreative(MusicInfo original) {
		MusicPitchApi.resetCurrentPitch();
		return original;
	}

	@ModifyReturnValue(method = "getSituationalMusic", at = @At(value = "RETURN", ordinal = 6))
	public MusicInfo frozenLib$resetPitchIfInMenu(MusicInfo original) {
		MusicPitchApi.resetCurrentPitch();
		return original;
	}

	@ModifyExpressionValue(
		method = "getSituationalMusic",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/sounds/Music;)Lnet/minecraft/client/sounds/MusicInfo;",
			ordinal = 2
		)
	)
	public MusicInfo frozenLib$getEndMusicAndPitch(
		MusicInfo original,
		@Share("frozenLib$biomeHolder") LocalRef<Holder<Biome>> biomeHolderRef
	) {
		Holder<Biome> biomeHolder = biomeHolderRef.get();
		if (biomeHolder != null) MusicPitchApi.updateTargetMusicPitch(this.player.level(), biomeHolder);
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
	public MusicInfo frozenLib$getNonCreativeMusicAndPitch(
		MusicInfo original,
		@Share("frozenLib$biomeHolder") LocalRef<Holder<Biome>> biomeHolderRef
	) {
		Holder<Biome> biomeHolder = biomeHolderRef.get();
		if (biomeHolder != null) MusicPitchApi.updateTargetMusicPitch(this.player.level(), biomeHolder);
		return StructureMusicApi.chooseMusicInfoOrStructureMusicInfo(this.player, original);
	}

}
