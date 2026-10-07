/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.music.pitch.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.levelgen.music.pitch.provider.PitchProvider;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@ApiStatus.Internal
@ClientOnly
public final class MusicPitchApi {
	//public static final Function<Long, Float> SUBTLE_PITCH_SHIFTING = (l) -> 0.99F + (Mth.sin((l * Mth.PI) / 1200) * 0.025F);
	private static float CURRENT_PITCH = 1F;

	public static void setCurrentPitch(float pitch) {
		CURRENT_PITCH = pitch;
	}

	public static void resetCurrentPitch() {
		setCurrentPitch(1F);
	}

	public static float getCurrentPitch() {
		return CURRENT_PITCH;
	}

	public static void updateTargetMusicPitch(@Nullable Player player, Level level, Holder<Biome> biome) {
		if (player == null) {
			resetCurrentPitch();
			return;
		}

		final List<Float> pitchSamples = new ArrayList<>();
		final AtomicInteger providerCount = new AtomicInteger();

		final Optional<StructureStatus> structureStatus = StructureStatus.getProminentStructureStatus(player);
		final PitchProvider.Context context = new PitchProvider.Context(
			level,
			level.dimension().identifier(),
			biome,
			structureStatus,
			player.position(),
			level.getGameTime()
		);
		level.registryAccess().lookup(FrozenLibRegistries.MUSIC_PITCH_PROVIDER).ifPresent(registry -> {
			for (PitchProvider provider : registry) {
				if (!provider.applicable(context)) continue;

				pitchSamples.add(provider.sample(context));
				providerCount.addAndGet(1);
			}
		});


		if (providerCount.get() <= 0) {
			resetCurrentPitch();
			return;
		}

		float totalPitchSamples = 0F;
		for (float sample : pitchSamples) totalPitchSamples += sample - 1F;

		setCurrentPitch(1F + (totalPitchSamples / providerCount.get()));
	}
}
