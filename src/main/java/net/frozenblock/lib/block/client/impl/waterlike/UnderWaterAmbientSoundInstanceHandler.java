package net.frozenblock.lib.block.client.impl.waterlike;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;

@Environment(EnvType.CLIENT)
public final class UnderWaterAmbientSoundInstanceHandler {
	private static final Map<WaterLikeType, AbstractTickableSoundInstance> WATER_LIKE_TYPE_SOUNDS = new Reference2ObjectArrayMap<>();
	private static final List<AbstractTickableSoundInstance> VANILLA_SOUNDS = new ArrayList<>();

	public static void tick() {
		WATER_LIKE_TYPE_SOUNDS.values().removeIf(Objects::isNull);
		WATER_LIKE_TYPE_SOUNDS.values().removeIf(AbstractTickableSoundInstance::isStopped);

		VANILLA_SOUNDS.removeIf(Objects::isNull);
		VANILLA_SOUNDS.removeIf(AbstractTickableSoundInstance::isStopped);
	}

	public static SoundEngine.PlayResult tryPlaySoundForType(WaterLikeType type, LocalPlayer player, SoundManager soundManager) {
		if (isPlayingSoundForType(type)) return SoundEngine.PlayResult.NOT_STARTED;
		return soundManager.play(new WaterLikeAmbientSoundInstance(type, player));
	}

	public static SoundEngine.PlayResult tryPlayVanillaSound(SoundInstance soundInstance, SoundManager soundManager) {
		if (isPlayingVanillaSound()) return SoundEngine.PlayResult.NOT_STARTED;
		return soundManager.play(soundInstance);
	}

	public static boolean isPlayingSoundForType(WaterLikeType type) {
		return WATER_LIKE_TYPE_SOUNDS.containsKey(type);
	}

	public static boolean isPlayingVanillaSound() {
		return !VANILLA_SOUNDS.isEmpty();
	}
}
