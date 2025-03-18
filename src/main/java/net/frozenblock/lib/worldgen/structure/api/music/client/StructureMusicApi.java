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

package net.frozenblock.lib.worldgen.structure.api.music.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.worldgen.structure.api.status.client.ClientStructureStatuses;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatus;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class StructureMusicApi {
	private static final Map<ResourceLocation, List<StructureMusic>> STRUCTURE_TO_MUSIC_MAP = new Object2ObjectLinkedOpenHashMap<>();

	/**
	 * Registers {@link StructureMusic} to be played while in a {@link Structure}.
	 *
	 * @param structureLocation The {@link ResourceLocation} of the {@link Structure} to play {@link StructureMusic} in.
	 * @param music The {@link StructureMusic} to play.
	 */
	public static void registerMusicForStructure(ResourceLocation structureLocation, StructureMusic music) {
		List<StructureMusic> musicList = STRUCTURE_TO_MUSIC_MAP.computeIfAbsent(structureLocation, location -> new ArrayList<>());
		musicList.add(music);
		STRUCTURE_TO_MUSIC_MAP.put(structureLocation, musicList);
	}

	/**
	 * Registers {@link StructureMusic} to be played while in a {@link Structure}.
	 *
	 * @param structureKey The {@link ResourceKey} of the {@link Structure} to play {@link StructureMusic} in.
	 * @param music The {@link StructureMusic} to play.
	 */
	public static void registerMusicForStructure(@NotNull ResourceKey<Structure> structureKey, StructureMusic music) {
		registerMusicForStructure(structureKey.location(), music);
	}

	@ApiStatus.Internal
	public static @NotNull Optional<Music> getCurrentStructureMusic(RandomSource random) {
		Optional<PlayerStructureStatus> optionalStructureStatus = ClientStructureStatuses.getProminentStructureStatus();
		if (optionalStructureStatus.isPresent()) {
			PlayerStructureStatus structureStatus = optionalStructureStatus.get();
			boolean isInsidePiece = structureStatus.isInsidePiece();

			List<StructureMusic> structureMusicList = STRUCTURE_TO_MUSIC_MAP.getOrDefault(structureStatus.getStructure(), List.of());
			List<StructureMusic> finalizedStructureMusicList = new ArrayList<>();

			for (StructureMusic structureMusic : structureMusicList) {
				if (isInsidePiece || !structureMusic.mustBeInsidePiece()) {
					finalizedStructureMusicList.add(structureMusic);
				}
			}

			if (!finalizedStructureMusicList.isEmpty()) {
				return Optional.of(Util.getRandom(finalizedStructureMusicList, random).music());
			}
		}

		return Optional.empty();
	}

	@ApiStatus.Internal
	public static @NotNull Music chooseMusicOrStructureMusic(@Nullable LocalPlayer player, Music music) {
		if (player == null) return music;
		return getCurrentStructureMusic(player.getRandom()).orElse(music);
	}
}
