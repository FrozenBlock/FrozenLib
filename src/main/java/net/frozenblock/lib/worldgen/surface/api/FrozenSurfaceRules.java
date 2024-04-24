/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.surface.api;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.worldgen.surface.impl.BiomeTagConditionSource;
import net.frozenblock.lib.worldgen.surface.impl.OptimizedBiomeTagConditionSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static SurfaceRules.SequenceRuleSource sequence(@NotNull List<SurfaceRules.RuleSource> list) {
        return new SurfaceRules.SequenceRuleSource(list);
    }

    public static SurfaceRules.ConditionSource isBiome(@NotNull List<ResourceKey<Biome>> biomes) {
        return SurfaceRules.isBiome(biomes);
    }

	public static SurfaceRules.ConditionSource isBiomeTag(@NotNull TagKey<Biome> biomeTagKey) {
		return new BiomeTagConditionSource(biomeTagKey);
	}

	public static SurfaceRules.ConditionSource isBiomeTagOptimized(@NotNull TagKey<Biome> biomeTagKey) {
		return new OptimizedBiomeTagConditionSource(biomeTagKey);
	}

    public static SurfaceRules.RuleSource makeStateRule(@NotNull Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

	@Nullable
	public static SurfaceRules.RuleSource getSurfaceRules(ResourceKey<DimensionType> dimension) {
		if (dimension == null) return null;

		var location = dimension.location();
		SurfaceRules.RuleSource returnValue = null;

		if (location.equals(BuiltinDimensionTypes.OVERWORLD.location()) || location.equals(BuiltinDimensionTypes.OVERWORLD_CAVES.location())) {
			returnValue = getOverworldSurfaceRules();
		} else if (location.equals(BuiltinDimensionTypes.NETHER.location())) {
			returnValue = getNetherSurfaceRules();
		} else if (location.equals(BuiltinDimensionTypes.END.location())) {
			returnValue = getEndSurfaceRules();
		}

		// get generic dimension surface rules
		SurfaceRules.RuleSource generic = getGenericSurfaceRules(dimension);

		if (generic != null) {
			if (returnValue == null) {
				returnValue = generic;
			} else {
				returnValue = SurfaceRules.sequence(returnValue, generic);
			}
		}

		return returnValue;
	}

	@Nullable
	public static SurfaceRules.RuleSource getOverworldSurfaceRules() {
		SurfaceRules.RuleSource newRule = null;
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addOverworldSurfaceRules(sourceHolders);
		SurfaceRules.RuleSource newSource = sequence(sourceHolders);

		newSource = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), newSource);
		newRule = newSource;

		// NO PRELIM

		ArrayList<SurfaceRules.RuleSource> noPrelimSourceHolders = new ArrayList<>();
		SurfaceRuleEvents.MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE.invoker().addOverworldNoPrelimSurfaceRules(noPrelimSourceHolders);

		SurfaceRules.RuleSource noPrelimSource = sequence(noPrelimSourceHolders);
		newRule = SurfaceRules.sequence(noPrelimSource, newRule);

		return newRule;
	}

	@Nullable
	public static SurfaceRules.RuleSource getNetherSurfaceRules() {
		SurfaceRules.RuleSource newSource = null;
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_NETHER.invoker().addNetherSurfaceRules(sourceHolders);

		for (SurfaceRules.RuleSource rule : sourceHolders) {
			if (newSource == null) {
				newSource = rule;
			} else {
				newSource = SurfaceRules.sequence(newSource, rule);
			}
		}

		return newSource;
	}

	@Nullable
	public static SurfaceRules.RuleSource getEndSurfaceRules() {
		SurfaceRules.RuleSource newSource = null;
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_END.invoker().addEndSurfaceRules(sourceHolders);

		for (SurfaceRules.RuleSource rule : sourceHolders) {
			if (newSource == null) {
				newSource = rule;
			} else {
				newSource = SurfaceRules.sequence(newSource, rule);
			}
		}

		return newSource;
	}

	@Nullable
	public static SurfaceRules.RuleSource getGenericSurfaceRules(ResourceKey<DimensionType> dimension) {
		SurfaceRules.RuleSource newSource = null;
		ArrayList<FrozenDimensionBoundRuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_GENERIC.invoker().addGenericSurfaceRules(sourceHolders);

		for (FrozenDimensionBoundRuleSource dimRuleSource : sourceHolders) {
			if (dimRuleSource.dimension().equals(dimension.location())) {
				if (newSource == null) {
					newSource = dimRuleSource.ruleSource();
				} else {
					newSource = SurfaceRules.sequence(newSource, dimRuleSource.ruleSource());
				}
			}
		}

		return newSource;
	}
}
