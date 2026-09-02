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

package net.frozenblock.lib.levelgen.material.api;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.BiomeCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

public final class FrozenLibMaterialRules {
	public static final MaterialRule AIR = makeStateRule(Blocks.AIR);
	public static final MaterialRule BEDROCK = makeStateRule(Blocks.BEDROCK);
	public static final MaterialRule WHITE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.white());
	public static final MaterialRule ORANGE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.orange());
	public static final MaterialRule TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
	public static final MaterialRule RED_SAND = makeStateRule(Blocks.RED_SAND);
	public static final MaterialRule RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
	public static final MaterialRule STONE = makeStateRule(Blocks.STONE);
	public static final MaterialRule DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
	public static final MaterialRule DIRT = makeStateRule(Blocks.DIRT);
	public static final MaterialRule PODZOL = makeStateRule(Blocks.PODZOL);
	public static final MaterialRule COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
	public static final MaterialRule MYCELIUM = makeStateRule(Blocks.MYCELIUM);
	public static final MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
	public static final MaterialRule CALCITE = makeStateRule(Blocks.CALCITE);
	public static final MaterialRule GRAVEL = makeStateRule(Blocks.GRAVEL);
	public static final MaterialRule SAND = makeStateRule(Blocks.SAND);
	public static final MaterialRule SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	public static final MaterialRule PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
	public static final MaterialRule SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
	public static final MaterialRule MUD = makeStateRule(Blocks.MUD);
	public static final MaterialRule POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
	public static final MaterialRule ICE = makeStateRule(Blocks.ICE);
	public static final MaterialRule WATER = makeStateRule(Blocks.WATER);
	public static final MaterialRule LAVA = makeStateRule(Blocks.LAVA);
	public static final MaterialRule NETHERRACK = makeStateRule(Blocks.NETHERRACK);
	public static final MaterialRule SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
	public static final MaterialRule SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
	public static final MaterialRule BASALT = makeStateRule(Blocks.BASALT);
	public static final MaterialRule BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
	public static final MaterialRule WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
	public static final MaterialRule WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
	public static final MaterialRule NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
	public static final MaterialRule CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
	public static final MaterialRule ENDSTONE = makeStateRule(Blocks.END_STONE);

	public static MaterialRule makeStateRule(Block block) {
		return MaterialRules.state(block.defaultBlockState());
	}

	public static BiomeCondition isBiomeTag(HolderGetter<Biome> biomes, TagKey<Biome> tagKey) {
		return new BiomeCondition(biomes.getOrThrow(tagKey));
	}

	public static BiomeCondition isBiomeTag(RegistryAccess registryAccess, TagKey<Biome> tagKey) {
		return isBiomeTag(registryAccess.lookupOrThrow(Registries.BIOME), tagKey);
	}
}
