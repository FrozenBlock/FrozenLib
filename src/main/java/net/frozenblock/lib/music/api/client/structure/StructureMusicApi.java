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

package net.frozenblock.lib.music.api.client.structure;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.worldgen.structure.api.status.client.ClientStructureStatuses;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatus;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@Environment(EnvType.CLIENT)
public class StructureMusicApi {
	private static final Map<Identifier, List<StructureMusic>> STRUCTURE_TO_MUSIC_MAP = new Object2ObjectLinkedOpenHashMap<>();

	/**
	 * Registers {@link StructureMusic} to be played while in a {@link Structure}.
	 *
	 * @param id The {@link Identifier} of the {@link Structure} to play {@link StructureMusic} in.
	 * @param music The {@link StructureMusic} to play.
	 */
	public static void registerMusicForStructure(Identifier id, StructureMusic music) {
		final List<StructureMusic> musicList = STRUCTURE_TO_MUSIC_MAP.computeIfAbsent(id, location -> new ArrayList<>());
		musicList.add(music);
		STRUCTURE_TO_MUSIC_MAP.put(id, musicList);
	}

	/**
	 * Registers {@link StructureMusic} to be played while in a {@link Structure}.
	 *
	 * @param structureKey The {@link ResourceKey} of the {@link Structure} to play {@link StructureMusic} in.
	 * @param music The {@link StructureMusic} to play.
	 */
	public static void registerMusicForStructure(ResourceKey<Structure> structureKey, StructureMusic music) {
		registerMusicForStructure(structureKey.identifier(), music);
	}

	@ApiStatus.Internal
	private static Optional<Music> getCurrentStructureMusic(RandomSource random) {
		final Optional<PlayerStructureStatus> optionalStructureStatus = ClientStructureStatuses.getProminentStructureStatus();
		if (optionalStructureStatus.isEmpty()) return Optional.empty();

		final PlayerStructureStatus structureStatus = optionalStructureStatus.get();
		final boolean isInsidePiece = structureStatus.isInsidePiece();

		final List<StructureMusic> structureMusicList = STRUCTURE_TO_MUSIC_MAP.getOrDefault(structureStatus.getStructure(), List.of());
		final List<StructureMusic> finalizedStructureMusicList = new ArrayList<>();

		for (StructureMusic structureMusic : structureMusicList) {
			if (isInsidePiece || !structureMusic.mustBeInsidePiece()) finalizedStructureMusicList.add(structureMusic);
		}

		if (!finalizedStructureMusicList.isEmpty()) return Optional.of(Util.getRandom(finalizedStructureMusicList, random).music());

		return Optional.empty();
	}

	@ApiStatus.Internal
	public static Music chooseMusicOrStructureMusic(@Nullable LocalPlayer player, Music music) {
		if (player == null) return music;
		return getCurrentStructureMusic(player.getRandom()).orElse(music);
	}
}
