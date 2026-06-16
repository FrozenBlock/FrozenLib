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

package net.frozenblock.lib.music.impl.structure;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.frozenblock.lib.sound.api.structure.StructureMusic;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@ApiStatus.Internal
public class StructureMusicSelector {

	private static Optional<BackgroundMusic> getCurrentStructureMusic(Player player, RegistryAccess registryAccess) {
		final Optional<StructureStatus> optionalStructureStatus = StructureStatus.getProminentStructureStatus(player);
		if (optionalStructureStatus.isEmpty()) return Optional.empty();

		final StructureStatus structureStatus = optionalStructureStatus.get();
		final Identifier structureId = structureStatus.structure();
		final boolean insidePiece = structureStatus.insidePiece();

		return registryAccess.lookupOrThrow(FrozenLibRegistries.STRUCTURE_MUSIC).stream()
			.filter(structureMusic -> insidePiece || !structureMusic.mustBeInsidePiece())
			.filter(structureMusic -> structureMusic.structures().stream().anyMatch(id -> id.equals(structureId)))
			.findFirst()
			.map(StructureMusic::backgroundMusic);
	}

	public static BackgroundMusic chooseStructureMusicOrOriginalMusic(@Nullable Player player, BackgroundMusic backgroundMusic) {
		if (player == null) return backgroundMusic;
		return getCurrentStructureMusic(player, player.registryAccess()).orElse(backgroundMusic);
	}
}
