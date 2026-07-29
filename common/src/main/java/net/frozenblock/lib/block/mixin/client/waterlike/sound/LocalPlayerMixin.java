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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import net.frozenblock.lib.block.client.impl.waterlike.UnderWaterAmbientSoundInstanceHandler;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderLiquidAmbientSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

	@Shadow
	@Final
	protected Minecraft minecraft;

	@Inject(method = "updateIsUnderwater", at = @At("HEAD"))
	public void frozenLib$setupWasInWaterLike(
		CallbackInfoReturnable<Boolean> info,
		@Share("frozenLib$wasPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> wasPlayerInWaterLikeStatuses
	) {
		wasPlayerInWaterLikeStatuses.set(LocalPlayer.class.cast(this).frozenLib$playerWaterLikesInside());
		UnderWaterAmbientSoundInstanceHandler.tick();
	}

	@Inject(
		method = "updateIsUnderwater",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/AbstractClientPlayer;updateIsUnderwater()Z",
			shift = At.Shift.AFTER
		)
	)
	public void frozenLib$setupIsInWaterLike(
		CallbackInfoReturnable<Boolean> info,
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> isPlayerInWaterLikeStatuses
	) {
		isPlayerInWaterLikeStatuses.set(LocalPlayer.class.cast(this).frozenLib$playerWaterLikesInside());
	}

	@ModifyExpressionValue(
		method = "updateIsUnderwater",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/sounds/SoundEvents;AMBIENT_UNDERWATER_ENTER:Lnet/minecraft/sounds/SoundEvent;",
			opcode = Opcodes.GETSTATIC
		)
	)
	public SoundEvent frozenLib$replaceWaterEnterSoundWithWaterLike(
		SoundEvent original,
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> isPlayerInWaterLikeStatuses
	) {
		if (isPlayerInWaterLikeStatuses != null && !isPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = isPlayerInWaterLikeStatuses.get();
			if (validTypes != null) return Util.getRandom(validTypes, LocalPlayer.class.cast(this).getRandom()).enterSound().value();
		}
		return original;
	}

	@WrapOperation(
		method = "updateIsUnderwater",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;"
		)
	)
	public SoundEngine.PlayResult frozenLib$replaceLoopSoundWithWaterLike(
		SoundManager instance, SoundInstance sound, Operation<SoundEngine.PlayResult> original,
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> isPlayerInWaterLikeStatuses
	) {
		if (isPlayerInWaterLikeStatuses != null && !isPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = isPlayerInWaterLikeStatuses.get();
			if (!validTypes.isEmpty()) {
				final WaterLikeType type = Util.getRandom(validTypes, LocalPlayer.class.cast(this).getRandom());
				return UnderWaterAmbientSoundInstanceHandler.tryPlaySoundForType(type, LocalPlayer.class.cast(this), instance);
			}
		}

		return UnderWaterAmbientSoundInstanceHandler.tryPlayVanillaSound(sound, instance);
	}

	@Inject(method = "updateIsUnderwater", at = @At(value = "RETURN", ordinal = 1))
	public void frozenLib$tryPlayAllUnderWaterLoopSounds(
		CallbackInfoReturnable<Boolean> info,
		@Local(name = "newIsUnderwater") boolean newIsUnderwater,
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> isPlayerInWaterLikeStatuses
	) {
		if (!newIsUnderwater) return;

		boolean hasAnyWaterLikeTypes = false;
		if (isPlayerInWaterLikeStatuses != null && !isPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = isPlayerInWaterLikeStatuses.get();
			for (WaterLikeType type : validTypes) {
				hasAnyWaterLikeTypes = true;
				UnderWaterAmbientSoundInstanceHandler.tryPlaySoundForType(type, LocalPlayer.class.cast(this), this.minecraft.getSoundManager());
			}
		}

		if (!hasAnyWaterLikeTypes) {
			UnderWaterAmbientSoundInstanceHandler.tryPlayVanillaSound(
				// TODO: see if we can just wrapOperation the underwater condition and invalidate if inside types with custom ambience
				UnderLiquidAmbientSoundInstance.underwater(LocalPlayer.class.cast(this)),
				this.minecraft.getSoundManager()
			);
		}
	}

	@ModifyExpressionValue(
		method = "updateIsUnderwater",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/sounds/SoundEvents;AMBIENT_UNDERWATER_EXIT:Lnet/minecraft/sounds/SoundEvent;",
			opcode = Opcodes.GETSTATIC
		)
	)
	public SoundEvent frozenLib$replaceWaterExitSoundWithWaterLike(
		SoundEvent original,
		@Share("frozenLib$wasPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> wasPlayerInWaterLikeStatuses
	) {
		if (wasPlayerInWaterLikeStatuses != null && !wasPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = wasPlayerInWaterLikeStatuses.get();
			if (validTypes != null) return Util.getRandom(validTypes, LocalPlayer.class.cast(this).getRandom()).exitSound().value();
		}
		return original;
	}
}
