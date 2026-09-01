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

package net.frozenblock.lib.block.client.impl.waterlike;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderLiquidAmbientSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;

@ClientOnly
public final class UnderWaterAmbientSoundInstanceHandler {
	private static final Map<WaterLikeType, UnderLiquidAmbientSoundInstance> WATER_LIKE_TYPE_SOUNDS = new Reference2ObjectArrayMap<>();
	private static final List<UnderLiquidAmbientSoundInstance> VANILLA_SOUNDS = new ArrayList<>();

	public static void tick() {
		WATER_LIKE_TYPE_SOUNDS.values().removeIf(Objects::isNull);
		WATER_LIKE_TYPE_SOUNDS.values().removeIf(UnderLiquidAmbientSoundInstance::isStopped);

		VANILLA_SOUNDS.removeIf(Objects::isNull);
		VANILLA_SOUNDS.removeIf(UnderLiquidAmbientSoundInstance::isStopped);
	}

	public static SoundEngine.PlayResult tryPlaySoundForType(WaterLikeType type, LocalPlayer player, SoundManager soundManager) {
		if (isPlayingSoundForType(type)) return SoundEngine.PlayResult.NOT_STARTED;
		final UnderLiquidAmbientSoundInstance waterLikeAmbientSound = new UnderLiquidAmbientSoundInstance(
			type.ambientSound().value(),
			player,
			localPlayer -> localPlayer.isUnderWater() && localPlayer.frozenLib$wasPlayerInWaterLike(type)
		);
		WATER_LIKE_TYPE_SOUNDS.put(type, waterLikeAmbientSound);
		return soundManager.play(waterLikeAmbientSound);
	}

	public static SoundEngine.PlayResult tryPlayVanillaSound(SoundInstance sound, SoundManager soundManager) {
		if (isPlayingVanillaSound()) return SoundEngine.PlayResult.NOT_STARTED;
		if (sound instanceof UnderLiquidAmbientSoundInstance tickable) VANILLA_SOUNDS.add(tickable);
		return soundManager.play(sound);
	}

	public static boolean isPlayingSoundForType(WaterLikeType type) {
		return WATER_LIKE_TYPE_SOUNDS.containsKey(type);
	}

	public static boolean isPlayingVanillaSound() {
		return !VANILLA_SOUNDS.isEmpty();
	}
}
