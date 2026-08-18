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

package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

@UtilityClass
public final class ConventionalBiomeTags {
	public static final TagKey<Biome> NO_DEFAULT_MONSTERS = bind("no_default_monsters");
	public static final TagKey<Biome> HIDDEN_FROM_LOCATOR_SELECTION = bind("hidden_from_locator_selection");
	public static final TagKey<Biome> IS_VOID = bind("is_void");
	public static final TagKey<Biome> IS_OVERWORLD = bind("is_overworld");
	public static final TagKey<Biome> IS_HOT = bind("is_hot");
	public static final TagKey<Biome> IS_HOT_OVERWORLD = bind("is_hot/overworld");
	public static final TagKey<Biome> IS_HOT_NETHER = bind("is_hot/nether");
	public static final TagKey<Biome> IS_HOT_END = bind("is_hot/end");
	public static final TagKey<Biome> IS_TEMPERATE = bind("is_temperate");
	public static final TagKey<Biome> IS_TEMPERATE_OVERWORLD = bind("is_temperate/overworld");
	public static final TagKey<Biome> IS_TEMPERATE_NETHER = bind("is_temperate/nether");
	public static final TagKey<Biome> IS_TEMPERATE_END = bind("is_temperate/end");
	public static final TagKey<Biome> IS_COLD = bind("is_cold");
	public static final TagKey<Biome> IS_COLD_OVERWORLD = bind("is_cold/overworld");
	public static final TagKey<Biome> IS_COLD_NETHER = bind("is_cold/nether");
	public static final TagKey<Biome> IS_COLD_END = bind("is_cold/end");
	public static final TagKey<Biome> IS_WET = bind("is_wet");
	public static final TagKey<Biome> IS_WET_OVERWORLD = bind("is_wet/overworld");
	public static final TagKey<Biome> IS_WET_NETHER = bind("is_wet/nether");
	public static final TagKey<Biome> IS_WET_END = bind("is_wet/end");
	public static final TagKey<Biome> IS_DRY = bind("is_dry");
	public static final TagKey<Biome> IS_DRY_OVERWORLD = bind("is_dry/overworld");
	public static final TagKey<Biome> IS_DRY_NETHER = bind("is_dry/nether");
	public static final TagKey<Biome> IS_DRY_END = bind("is_dry/end");
	public static final TagKey<Biome> IS_VEGETATION_SPARSE = bind("is_sparse_vegetation");
	public static final TagKey<Biome> IS_VEGETATION_SPARSE_OVERWORLD = bind("is_sparse_vegetation/overworld");
	public static final TagKey<Biome> IS_VEGETATION_SPARSE_NETHER = bind("is_sparse_vegetation/nether");
	public static final TagKey<Biome> IS_VEGETATION_SPARSE_END = bind("is_sparse_vegetation/end");
	public static final TagKey<Biome> IS_VEGETATION_DENSE = bind("is_dense_vegetation");
	public static final TagKey<Biome> IS_VEGETATION_DENSE_OVERWORLD = bind("is_dense_vegetation/overworld");
	public static final TagKey<Biome> IS_VEGETATION_DENSE_NETHER = bind("is_dense_vegetation/nether");
	public static final TagKey<Biome> IS_VEGETATION_DENSE_END = bind("is_dense_vegetation/end");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE = bind("primary_wood_type");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_OAK = bind("primary_wood_type/oak");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_BIRCH = bind("primary_wood_type/birch");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_SPRUCE = bind("primary_wood_type/spruce");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_JUNGLE = bind("primary_wood_type/jungle");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_ACACIA = bind("primary_wood_type/acacia");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_DARK_OAK = bind("primary_wood_type/dark_oak");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_MANGROVE = bind("primary_wood_type/mangrove");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_CHERRY = bind("primary_wood_type/cherry");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_PALE_OAK = bind("primary_wood_type/pale_oak");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_BAMBOO = bind("primary_wood_type/bamboo");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_CRIMSON = bind("primary_wood_type/crimson");
	public static final TagKey<Biome> PRIMARY_WOOD_TYPE_WARPED = bind("primary_wood_type/warped");
	public static final TagKey<Biome> IS_CONIFEROUS_TREE = bind("is_tree/coniferous");
	public static final TagKey<Biome> IS_SAVANNA_TREE = bind("is_tree/savanna");
	public static final TagKey<Biome> IS_JUNGLE_TREE = bind("is_tree/jungle");
	public static final TagKey<Biome> IS_DECIDUOUS_TREE = bind("is_tree/deciduous");
	public static final TagKey<Biome> IS_MOUNTAIN = bind("is_mountain");
	public static final TagKey<Biome> IS_MOUNTAIN_PEAK = bind("is_mountain/peak");
	public static final TagKey<Biome> IS_MOUNTAIN_SLOPE = bind("is_mountain/slope");
	public static final TagKey<Biome> IS_PLAINS = bind("is_plains");
	public static final TagKey<Biome> IS_SNOWY_PLAINS = bind("is_snowy_plains");
	public static final TagKey<Biome> IS_FOREST = bind("is_forest");
	public static final TagKey<Biome> IS_BIRCH_FOREST = bind("is_birch_forest");
	public static final TagKey<Biome> IS_DARK_FOREST = bind("is_dark_forest");
	public static final TagKey<Biome> IS_FLOWER_FOREST = bind("is_flower_forest");
	public static final TagKey<Biome> IS_TAIGA = bind("is_taiga");
	public static final TagKey<Biome> IS_OLD_GROWTH = bind("is_old_growth");
	public static final TagKey<Biome> IS_HILL = bind("is_hill");
	public static final TagKey<Biome> IS_WINDSWEPT = bind("is_windswept");
	public static final TagKey<Biome> IS_JUNGLE = bind("is_jungle");
	public static final TagKey<Biome> IS_SAVANNA = bind("is_savanna");
	public static final TagKey<Biome> IS_SWAMP = bind("is_swamp");
	public static final TagKey<Biome> IS_DESERT = bind("is_desert");
	public static final TagKey<Biome> IS_BADLANDS = bind("is_badlands");
	public static final TagKey<Biome> IS_BEACH = bind("is_beach");
	public static final TagKey<Biome> IS_STONY_SHORES = bind("is_stony_shores");
	public static final TagKey<Biome> IS_MUSHROOM = bind("is_mushroom");
	public static final TagKey<Biome> IS_RIVER = bind("is_river");
	public static final TagKey<Biome> IS_OCEAN = bind("is_ocean");
	public static final TagKey<Biome> IS_DEEP_OCEAN = bind("is_deep_ocean");
	public static final TagKey<Biome> IS_SHALLOW_OCEAN = bind("is_shallow_ocean");
	public static final TagKey<Biome> IS_UNDERGROUND = bind("is_underground");
	public static final TagKey<Biome> IS_CAVE = bind("is_cave");
	public static final TagKey<Biome> IS_WASTELAND = bind("is_wasteland");
	public static final TagKey<Biome> IS_DEAD = bind("is_dead");
	public static final TagKey<Biome> IS_LUSH = bind("is_lush");
	public static final TagKey<Biome> IS_MAGICAL = bind("is_magical");
	public static final TagKey<Biome> IS_RARE = bind("is_rare");
	public static final TagKey<Biome> IS_PLATEAU = bind("is_plateau");
	public static final TagKey<Biome> IS_SPOOKY = bind("is_spooky");
	public static final TagKey<Biome> IS_FLORAL = bind("is_floral");
	public static final TagKey<Biome> IS_SANDY = bind("is_sandy");
	public static final TagKey<Biome> IS_SNOWY = bind("is_snowy");
	public static final TagKey<Biome> IS_ICY = bind("is_icy");
	public static final TagKey<Biome> IS_AQUATIC = bind("is_aquatic");
	public static final TagKey<Biome> IS_AQUATIC_ICY = bind("is_aquatic_icy");
	public static final TagKey<Biome> IS_NETHER = bind("is_nether");
	public static final TagKey<Biome> IS_NETHER_FOREST = bind("is_nether_forest");
	public static final TagKey<Biome> IS_END = bind("is_end");
	public static final TagKey<Biome> IS_OUTER_END_ISLAND = bind("is_outer_end_island");

	private static TagKey<Biome> bind(String path) {
		return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", path));
	}
}
