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

package net.frozenblock.lib.music.api.client.pitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.music.impl.client.MusicPitchDetectionType;
import net.frozenblock.lib.worldgen.structure.api.status.client.ClientStructureStatuses;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatus;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class MusicPitchApi {
	public static Function<Long, Float> SUBTLE_PITCH_SHIFTING = (l) -> 0.99F + (Mth.sin((l * Mth.PI) / 1200) * 0.025F);
	private static final List<MusicPitchInfo> MUSIC_PITCH_INFO_LIST = new ArrayList<>();
	private static float CURRENT_PITCH = 1F;

	public static void registerForBiome(ResourceLocation location, Function<Long, Float> pitchFunction) {
		MUSIC_PITCH_INFO_LIST.add(new MusicPitchInfo(MusicPitchDetectionType.BIOME, location, pitchFunction));
	}

	public static void registerForBiome(ResourceLocation location, float pitch) {
		registerForBiome(location, (l) -> pitch);
	}

	public static void registerForStructure(ResourceLocation location, Function<Long, Float> pitchFunction) {
		MUSIC_PITCH_INFO_LIST.add(new MusicPitchInfo(MusicPitchDetectionType.STRUCTURE, location, pitchFunction));
	}

	public static void registerForStructure(ResourceLocation location, float pitch) {
		registerForStructure(location, (l) -> pitch);
	}

	public static void registerForStructureInside(ResourceLocation location, Function<Long, Float> pitchFunction) {
		MUSIC_PITCH_INFO_LIST.add(new MusicPitchInfo(MusicPitchDetectionType.STRUCTURE_INSIDE, location, pitchFunction));
	}

	public static void registerForStructureInside(ResourceLocation location, float pitch) {
		registerForStructureInside(location, (l) -> pitch);
	}

	public static void registerForDimension(ResourceLocation location, Function<Long, Float> pitchFunction) {
		MUSIC_PITCH_INFO_LIST.add(new MusicPitchInfo(MusicPitchDetectionType.DIMENSION, location, pitchFunction));
	}

	public static void registerForDimension(ResourceLocation location, float pitch) {
		registerForDimension(location, (l) -> pitch);
	}

	@ApiStatus.Internal
	public static void setCurrentPitch(float pitch) {
		CURRENT_PITCH = pitch;
	}

	@ApiStatus.Internal
	public static void resetCurrentPitch() {
		setCurrentPitch(1F);
	}

	@ApiStatus.Internal
	public static float getCurrentPitch() {
		return CURRENT_PITCH;
	}

	@ApiStatus.Internal
	public static void updateTargetMusicPitch(@NotNull Level level, Holder<Biome> biome) {
		long gameTime = level.getGameTime();

		List<Float> pitches = new ArrayList<>();
		int pitchContributors = 0;

		Optional<PlayerStructureStatus> optionalStructureStatus = ClientStructureStatuses.getProminentStructureStatus();

		for (MusicPitchInfo info : MUSIC_PITCH_INFO_LIST) {
			if (info.type().isForStructure()) {
				if (optionalStructureStatus.isPresent()) {
					PlayerStructureStatus structureStatus = optionalStructureStatus.get();
					ResourceLocation structureLocation = structureStatus.getStructure();
					boolean isInsidePiece = structureStatus.isInsidePiece();

					if (info.type().isForStructureAndMatchesInside(isInsidePiece) && info.location().equals(structureLocation)) {
						pitches.add(info.pitchFunction().apply(gameTime));
						pitchContributors += 1;
					}
				}
			}

			if (info.type().isForBiome() && biome.is(info.location())) {
				pitches.add(info.pitchFunction().apply(gameTime));
				pitchContributors += 1;
			}

			if (info.type().isForDimension() && level.dimension().location().equals(info.location())) {
				pitches.add(info.pitchFunction().apply(gameTime));
				pitchContributors += 1;
			}
		}

		if (pitchContributors <= 0) {
			resetCurrentPitch();
			return;
		}

		float totalPitches = 0F;
		for (float suppliedPitch : pitches) {
			totalPitches += suppliedPitch;
		}

		setCurrentPitch(totalPitches / pitchContributors);
	}
}
