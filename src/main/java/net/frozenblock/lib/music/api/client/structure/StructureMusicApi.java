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

package net.frozenblock.lib.music.api.client.structure;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.worldgen.structure.api.status.client.ClientStructureStatuses;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatus;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.Util;

@Environment(EnvType.CLIENT)
public class StructureMusicApi {
	private static final Map<ResourceLocation, List<StructureMusicInfo>> STRUCTURE_TO_MUSIC_INFO_MAP = new Object2ObjectLinkedOpenHashMap<>();

	/**
	 * Registers {@link StructureMusicInfo} to be played while in a {@link Structure}.
	 *
	 * @param structureLocation The {@link ResourceLocation} of the {@link Structure} to play {@link StructureMusicInfo} in.
	 * @param musicInfo The {@link StructureMusicInfo} to play.
	 */
	public static void registerMusicInfoForStructure(ResourceLocation structureLocation, StructureMusicInfo musicInfo) {
		List<StructureMusicInfo> musicList = STRUCTURE_TO_MUSIC_INFO_MAP.computeIfAbsent(structureLocation, location -> new ArrayList<>());
		musicList.add(musicInfo);
		STRUCTURE_TO_MUSIC_INFO_MAP.put(structureLocation, musicList);
	}

	/**
	 * Registers {@link StructureMusicInfo} to be played while in a {@link Structure}.
	 *
	 * @param structureKey The {@link ResourceKey} of the {@link Structure} to play {@link StructureMusicInfo} in.
	 * @param musicInfo The {@link StructureMusicInfo} to play.
	 */
	public static void registerMusicInfoForStructure(@NotNull ResourceKey<Structure> structureKey, StructureMusicInfo musicInfo) {
		registerMusicInfoForStructure(structureKey.location(), musicInfo);
	}

	@ApiStatus.Internal
	private static @NotNull Optional<MusicInfo> getCurrentStructureMusicInfo(RandomSource random) {
		Optional<PlayerStructureStatus> optionalStructureStatus = ClientStructureStatuses.getProminentStructureStatus();
		if (optionalStructureStatus.isPresent()) {
			PlayerStructureStatus structureStatus = optionalStructureStatus.get();
			boolean isInsidePiece = structureStatus.isInsidePiece();

			List<StructureMusicInfo> structureMusicInfoList = STRUCTURE_TO_MUSIC_INFO_MAP.getOrDefault(structureStatus.getStructure(), List.of());
			List<StructureMusicInfo> finalizedStructureMusicInfoList = new ArrayList<>();

			for (StructureMusicInfo structureMusicInfo : structureMusicInfoList) {
				if (isInsidePiece || !structureMusicInfo.mustBeInsidePiece()) {
					finalizedStructureMusicInfoList.add(structureMusicInfo);
				}
			}

			if (!finalizedStructureMusicInfoList.isEmpty()) {
				return Optional.of(Util.getRandom(finalizedStructureMusicInfoList, random).musicInfo());
			}
		}

		return Optional.empty();
	}

	@ApiStatus.Internal
	public static @NotNull MusicInfo chooseMusicInfoOrStructureMusicInfo(@Nullable LocalPlayer player, MusicInfo musicInfo) {
		if (player == null) return musicInfo;
		return getCurrentStructureMusicInfo(player.getRandom()).orElse(musicInfo);
	}
}
