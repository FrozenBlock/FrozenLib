package net.frozenblock.lib.block.mixin.client.waterlike.sound;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.impl.waterlike.UnderWaterAmbientSoundInstanceHandler;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
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

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

	@Shadow
	@Final
	protected Minecraft minecraft;

	@Inject(method = "updateIsUnderwater", at = @At("HEAD"))
	public void frozenLib$setupWasInWaterLike(
		CallbackInfoReturnable<Boolean> info,
		@Share("frozenLib$wasPlayerInWaterLikeStatuses") LocalRef<Map<WaterLikeType, Boolean>> wasPlayerInWaterLikeStatuses
	) {
		wasPlayerInWaterLikeStatuses.set(LocalPlayer.class.cast(this).frozenLib$playerInWaterLikeStatuses());
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
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<Map<WaterLikeType, Boolean>> isPlayerInWaterLikeStatuses
	) {
		isPlayerInWaterLikeStatuses.set(LocalPlayer.class.cast(this).frozenLib$playerInWaterLikeStatuses());
	}

	@ModifyExpressionValue(
		method = "updateIsUnderwater",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/sounds/SoundEvents;AMBIENT_UNDERWATER_ENTER:Lnet/minecraft/sounds/SoundEvent;",
			opcode = Opcodes.GETSTATIC
		)
	)
	public SoundEvent frozenLib$replaceWaterEnterSoundWithWaterLIke(
		SoundEvent original,
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<Map<WaterLikeType, Boolean>> isPlayerInWaterLikeStatuses
	) {
		if (isPlayerInWaterLikeStatuses != null && !isPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = isPlayerInWaterLikeStatuses.get().entrySet().stream()
				.filter(entry -> entry.getValue() == true)
				.map(Map.Entry::getKey)
				.toList();
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
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<Map<WaterLikeType, Boolean>> isPlayerInWaterLikeStatuses
	) {
		if (isPlayerInWaterLikeStatuses != null && !isPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = isPlayerInWaterLikeStatuses.get().entrySet().stream()
				.filter(entry -> entry.getValue() == true)
				.map(Map.Entry::getKey)
				.toList();
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
		@Share("frozenLib$isPlayerInWaterLikeStatuses") LocalRef<Map<WaterLikeType, Boolean>> isPlayerInWaterLikeStatuses
	) {
		if (!newIsUnderwater) return;

		boolean hasAnyWaterLikeTypes = false;
		if (isPlayerInWaterLikeStatuses != null && !isPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = isPlayerInWaterLikeStatuses.get().entrySet().stream()
				.filter(entry -> entry.getValue() == true)
				.map(Map.Entry::getKey)
				.toList();
			for (WaterLikeType type : validTypes) {
				hasAnyWaterLikeTypes = true;
				UnderWaterAmbientSoundInstanceHandler.tryPlaySoundForType(type, LocalPlayer.class.cast(this), this.minecraft.getSoundManager());
			}
		}

		if (!hasAnyWaterLikeTypes)  {
			UnderWaterAmbientSoundInstanceHandler.tryPlayVanillaSound(
				new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(LocalPlayer.class.cast(this)),
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
		@Share("frozenLib$wasPlayerInWaterLikeStatuses") LocalRef<Map<WaterLikeType, Boolean>> wasPlayerInWaterLikeStatuses
	) {
		if (wasPlayerInWaterLikeStatuses != null && !wasPlayerInWaterLikeStatuses.get().isEmpty()) {
			final List<WaterLikeType> validTypes = wasPlayerInWaterLikeStatuses.get().entrySet().stream()
				.filter(entry -> entry.getValue() == true)
				.map(Map.Entry::getKey)
				.toList();
			if (validTypes != null) return Util.getRandom(validTypes, LocalPlayer.class.cast(this).getRandom()).exitSound().value();
		}
		return original;
	}

}
