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
import net.mehvahdjukaar.candlelight.api.ClientOnly;
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
	public void frozenLib$setupWasAndIsInWaterLikes(
		CallbackInfoReturnable<Boolean> info,
		@Share("frozenLib$wasPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> wasPlayerInWaterLikeStatuses,
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<List<WaterLikeType>> isPlayerInWaterLikeStatuses
	) {
		wasPlayerInWaterLikeStatuses.set(LocalPlayer.class.cast(this).frozenLib$wasPlayerEyesInsideWaterLikes());
		isPlayerInWaterLikeStatuses.set(LocalPlayer.class.cast(this).frozenLib$playerEyesInsideWaterLikes());
		UnderWaterAmbientSoundInstanceHandler.tick();
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
		if (isPlayerInWaterLikeStatuses == null) return original;

		final List<WaterLikeType> types = isPlayerInWaterLikeStatuses.get();
		if (types == null || types.isEmpty()) return original;

		return Util.getRandom(types, LocalPlayer.class.cast(this).getRandom()).enterSound().value();
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
		tryPlayerWaterLikeTypeLoop: {
			if (isPlayerInWaterLikeStatuses == null) break tryPlayerWaterLikeTypeLoop;

			final List<WaterLikeType> types = isPlayerInWaterLikeStatuses.get();
			if (types == null || types.isEmpty()) break tryPlayerWaterLikeTypeLoop;

			final WaterLikeType type = Util.getRandom(types, LocalPlayer.class.cast(this).getRandom());
			return UnderWaterAmbientSoundInstanceHandler.tryPlaySoundForType(type, LocalPlayer.class.cast(this), instance);
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
		tryPlayerAllWaterLikeTypeLoops: {
			if (isPlayerInWaterLikeStatuses == null) break tryPlayerAllWaterLikeTypeLoops;

			final List<WaterLikeType> types = isPlayerInWaterLikeStatuses.get();
			if (types == null || types.isEmpty()) break tryPlayerAllWaterLikeTypeLoops;

			for (WaterLikeType type : types) {
				hasAnyWaterLikeTypes = true;
				UnderWaterAmbientSoundInstanceHandler.tryPlaySoundForType(type, LocalPlayer.class.cast(this), this.minecraft.getSoundManager());
			}
		}

		if (!hasAnyWaterLikeTypes) {
			UnderWaterAmbientSoundInstanceHandler.tryPlayVanillaSound(
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
		if (wasPlayerInWaterLikeStatuses == null) return original;

		final List<WaterLikeType> types = wasPlayerInWaterLikeStatuses.get();
		if (types == null || types.isEmpty()) return original;

		return Util.getRandom(types, LocalPlayer.class.cast(this).getRandom()).exitSound().value();
	}
}
