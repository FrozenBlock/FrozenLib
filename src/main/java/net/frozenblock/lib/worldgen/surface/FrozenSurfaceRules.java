/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.surface;

import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class FrozenSurfaceRules {

	public static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
	public static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
	public static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
	public static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
	public static final SurfaceRules.RuleSource TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
	public static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
	public static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
	public static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
	public static final SurfaceRules.RuleSource DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
	public static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
	public static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
	public static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
	public static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
	public static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
	public static final SurfaceRules.RuleSource CALCITE = makeStateRule(Blocks.CALCITE);
	public static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
	public static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
	public static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	public static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
	public static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
	public static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
	public static final SurfaceRules.RuleSource POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
	public static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
	public static final SurfaceRules.RuleSource WATER = makeStateRule(Blocks.WATER);
	public static final SurfaceRules.RuleSource LAVA = makeStateRule(Blocks.LAVA);
	public static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
	public static final SurfaceRules.RuleSource SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
	public static final SurfaceRules.RuleSource SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
	public static final SurfaceRules.RuleSource BASALT = makeStateRule(Blocks.BASALT);
	public static final SurfaceRules.RuleSource BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
	public static final SurfaceRules.RuleSource WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
	public static final SurfaceRules.RuleSource WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
	public static final SurfaceRules.RuleSource NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
	public static final SurfaceRules.RuleSource CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
	public static final SurfaceRules.RuleSource ENDSTONE = makeStateRule(Blocks.END_STONE);

	public static SurfaceRules.SequenceRuleSource sequence(List<SurfaceRules.RuleSource> list) {
		return new SurfaceRules.SequenceRuleSource(list);
	}

	public static SurfaceRules.ConditionSource isBiome(List<ResourceKey<Biome>> biomes) {
		return SurfaceRules.isBiome(biomes);
	}

	public static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}
}
