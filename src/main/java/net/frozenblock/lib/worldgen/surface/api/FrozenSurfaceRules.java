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
import net.minecraft.world.level.levelgen.VerticalAnchor;
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

	public static SurfaceRules.SequenceRuleSource sequence(List<SurfaceRules.RuleSource> list) {
		return new SurfaceRules.SequenceRuleSource(list);
	}

	public static SurfaceRules.ConditionSource isBiome(List<ResourceKey<Biome>> biomes) {
		return SurfaceRules.isBiome(biomes);
	}

	public static SurfaceRules.ConditionSource isBiomeTag(TagKey<Biome> biomeTagKey) {
		return new BiomeTagConditionSource(biomeTagKey);
	}

	public static SurfaceRules.ConditionSource isBiomeTagOptimized(TagKey<Biome> biomeTagKey) {
		return new OptimizedBiomeTagConditionSource(biomeTagKey);
	}

	public static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	@Nullable
	public static SurfaceRules.RuleSource getSurfaceRules(ResourceKey<DimensionType> dimension) {
		if (dimension == null) return null;

		final var location = dimension.identifier();
		SurfaceRules.RuleSource returnValue = null;

		if (location.equals(BuiltinDimensionTypes.OVERWORLD.identifier()) || location.equals(BuiltinDimensionTypes.OVERWORLD_CAVES.identifier())) {
			returnValue = getOverworldSurfaceRules();
		} else if (location.equals(BuiltinDimensionTypes.NETHER.identifier())) {
			returnValue = getNetherSurfaceRules();
		} else if (location.equals(BuiltinDimensionTypes.END.identifier())) {
			returnValue = getEndSurfaceRules();
		}

		// Get generic dimension surface rules
		final SurfaceRules.RuleSource generic = getGenericSurfaceRules(dimension);
		if (generic != null) returnValue = returnValue == null ? generic : SurfaceRules.sequence(returnValue, generic);

		return returnValue;
	}

	@Nullable
	public static SurfaceRules.RuleSource getOverworldSurfaceRules() {
		SurfaceRules.RuleSource newRule = null;

		final ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();
		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addOverworldSurfaceRules(sourceHolders);

		if (!sourceHolders.isEmpty()) {
			SurfaceRules.RuleSource newSource = sequence(sourceHolders);
			newSource = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), newSource);
			newRule = newSource;
		}

		// NO PRELIMINARY SURFACE
		final ArrayList<SurfaceRules.RuleSource> noPrelimSourceHolders = new ArrayList<>();
		SurfaceRuleEvents.MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE.invoker().addOverworldNoPrelimSurfaceRules(noPrelimSourceHolders);

		if (!noPrelimSourceHolders.isEmpty()) {
			final SurfaceRules.RuleSource noPrelimSource = sequence(noPrelimSourceHolders);
			newRule = newRule == null ? noPrelimSource : SurfaceRules.sequence(noPrelimSource, newRule);
		}

		return newRule;
	}

	@Nullable
	public static SurfaceRules.RuleSource getNetherSurfaceRules() {
		SurfaceRules.RuleSource newSource = null;

		final ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();
		SurfaceRuleEvents.MODIFY_NETHER.invoker().addNetherSurfaceRules(sourceHolders);

		if (!sourceHolders.isEmpty()) {
			newSource = SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.verticalGradient("frozenlib_bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
				SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("frozenlib_bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK),
				sequence(sourceHolders)
			);
		}

		return newSource;
	}

	@Nullable
	public static SurfaceRules.RuleSource getEndSurfaceRules() {
		SurfaceRules.RuleSource newSource = null;

		final ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();
		SurfaceRuleEvents.MODIFY_END.invoker().addEndSurfaceRules(sourceHolders);

		if (!sourceHolders.isEmpty()) newSource = sequence(sourceHolders);

		return newSource;
	}

	@Nullable
	public static SurfaceRules.RuleSource getGenericSurfaceRules(ResourceKey<DimensionType> dimension) {
		SurfaceRules.RuleSource newSource = null;
		final ArrayList<FrozenDimensionBoundRuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_GENERIC.invoker().addGenericSurfaceRules(sourceHolders);
		final List<SurfaceRules.RuleSource> sourceHoldersForDimension = sourceHolders
			.stream()
			.filter(dimRuleSource -> dimRuleSource.dimension().equals(dimension.identifier()))
			.map(FrozenDimensionBoundRuleSource::ruleSource)
			.toList();

		if (!sourceHoldersForDimension.isEmpty()) newSource = sequence(sourceHoldersForDimension);

		return newSource;
	}
}
