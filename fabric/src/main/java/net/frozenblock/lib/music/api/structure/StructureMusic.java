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

package net.frozenblock.lib.music.api.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

/**
 * @param structures The {@link Identifier}s of the relevant {@link Structure}s for the music to play it.
 * @param backgroundMusic The {@link BackgroundMusic} to play while in a {@link Structure}.
 * @param mustBeInsidePiece Whether this can play only while the {@link Player} is directly inside a {@link StructurePiece}.
 * @param configPredicate The {@link ConfigPredicate} to test. This instance will be ignored if it returns false.
 */
public record StructureMusic(List<Identifier> structures, BackgroundMusic backgroundMusic, boolean mustBeInsidePiece, Optional<ConfigPredicate> configPredicate) {
	public static final Codec<StructureMusic> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Identifier.CODEC.listOf().fieldOf("structures").forGetter(StructureMusic::structures),
		BackgroundMusic.CODEC.fieldOf("background_music").forGetter(StructureMusic::backgroundMusic),
		Codec.BOOL.optionalFieldOf("must_be_inside_piece", false).forGetter(StructureMusic::mustBeInsidePiece),
		ConfigPredicate.CODEC.optionalFieldOf("config_predicate").forGetter(StructureMusic::configPredicate)
	).apply(instance, StructureMusic::new));

	public static ResourceKey<StructureMusic> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.STRUCTURE_MUSIC, id);
	}

	public static void register(
		BootstrapContext<StructureMusic> context,
		ResourceKey<StructureMusic> name,
		Identifier structure,
		BackgroundMusic backgroundMusic
	) {
		register(context, name, structure, backgroundMusic, false);
	}

	public static void register(
		BootstrapContext<StructureMusic> context,
		ResourceKey<StructureMusic> name,
		List<Identifier> structures,
		BackgroundMusic backgroundMusic
	) {
		register(context, name, structures, backgroundMusic, false);
	}

	public static void register(
		BootstrapContext<StructureMusic> context,
		ResourceKey<StructureMusic> name,
		Identifier structure,
		BackgroundMusic backgroundMusic,
		boolean mustBeInsidePiece
	) {
		register(context, name, List.of(structure), backgroundMusic, mustBeInsidePiece, Optional.empty());
	}

	public static void register(
		BootstrapContext<StructureMusic> context,
		ResourceKey<StructureMusic> name,
		List<Identifier> structures,
		BackgroundMusic backgroundMusic,
		boolean mustBeInsidePiece
	) {
		register(context, name, structures, backgroundMusic, mustBeInsidePiece, Optional.empty());
	}

	public static void register(
		BootstrapContext<StructureMusic> context,
		ResourceKey<StructureMusic> name,
		Identifier structure,
		BackgroundMusic backgroundMusic,
		boolean mustBeInsidePiece,
		ConfigPredicate configPredicate
	) {
		register(context, name, List.of(structure), backgroundMusic, mustBeInsidePiece, Optional.of(configPredicate));
	}

	public static void register(
		BootstrapContext<StructureMusic> context,
		ResourceKey<StructureMusic> name,
		List<Identifier> structures,
		BackgroundMusic backgroundMusic,
		boolean mustBeInsidePiece,
		ConfigPredicate configPredicate
	) {
		register(context, name, structures, backgroundMusic, mustBeInsidePiece, Optional.of(configPredicate));
	}

	public static void register(
		BootstrapContext<StructureMusic> context,
		ResourceKey<StructureMusic> name,
		List<Identifier> structures,
		BackgroundMusic backgroundMusic,
		boolean mustBeInsidePiece,
		Optional<ConfigPredicate> configPredicate
	) {
		context.register(name, new StructureMusic(structures, backgroundMusic, mustBeInsidePiece, configPredicate));
	}
}
