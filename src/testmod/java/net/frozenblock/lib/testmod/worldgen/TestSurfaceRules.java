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

package net.frozenblock.lib.testmod.worldgen;

import java.util.List;
import net.frozenblock.lib.worldgen.surface.api.FrozenDimensionBoundRuleSource;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class TestSurfaceRules implements SurfaceRuleEvents.OverworldSurfaceRuleCallback,
	SurfaceRuleEvents.NetherSurfaceRuleCallback, SurfaceRuleEvents.EndSurfaceRuleCallback,
	SurfaceRuleEvents.GenericSurfaceRuleCallback {
    @Override
    public void addOverworldSurfaceRules(HolderLookup<Biome> biomes, List<SurfaceRules.RuleSource> context) {
        SurfaceRules.ConditionSource greenNoise = SurfaceRules.noiseCondition3d(Noises.CALCITE, 0.05, 0.1);
        SurfaceRules.ConditionSource orangeNoise = SurfaceRules.noiseCondition3d(Noises.CALCITE, 0.1, 0.15);
        SurfaceRules.ConditionSource whiteNoise = SurfaceRules.noiseCondition3d(Noises.CALCITE, 0.15, 0.20);
        SurfaceRules.ConditionSource redNoise = SurfaceRules.noiseCondition3d(Noises.CALCITE, 0.20, 0.25);
        SurfaceRules.ConditionSource cyanNoise = SurfaceRules.noiseCondition3d(Noises.CALCITE, 0.25, 0.30);
		SurfaceRules.ConditionSource purpleNoise = SurfaceRules.noiseCondition3d(Noises.CALCITE, 0.30, 0.35);
		SurfaceRules.ConditionSource waterNoise = SurfaceRules.noiseCondition3d(Noises.CALCITE, 0.35, 0.40);

        SurfaceRules.RuleSource greenConcrete = SurfaceRules.state(Blocks.CONCRETE.green().defaultBlockState());
        SurfaceRules.RuleSource orangeConcrete = SurfaceRules.state(Blocks.CONCRETE.orange().defaultBlockState());
        SurfaceRules.RuleSource whiteConcrete = SurfaceRules.state(Blocks.CONCRETE.white().defaultBlockState());
		SurfaceRules.RuleSource redConcrete = SurfaceRules.state(Blocks.CONCRETE.red().defaultBlockState());
		SurfaceRules.RuleSource cyanConcrete = SurfaceRules.state(Blocks.CONCRETE.cyan().defaultBlockState());
		SurfaceRules.RuleSource purpleConcrete = SurfaceRules.state(Blocks.CONCRETE.purple().defaultBlockState());
		SurfaceRules.RuleSource water = SurfaceRules.state(Blocks.WATER.defaultBlockState());

        context.add(
			SurfaceRules.ifTrue(
				SurfaceRules.abovePreliminarySurface(),
				SurfaceRules.ifTrue(
					SurfaceRules.ON_FLOOR,
					SurfaceRules.sequence(
						SurfaceRules.ifTrue(greenNoise, greenConcrete),
						SurfaceRules.ifTrue(orangeNoise, orangeConcrete),
						SurfaceRules.ifTrue(whiteNoise, whiteConcrete),
						SurfaceRules.ifTrue(redNoise, redConcrete),
						SurfaceRules.ifTrue(cyanNoise, cyanConcrete),
						SurfaceRules.ifTrue(purpleNoise, purpleConcrete),
						SurfaceRules.ifTrue(waterNoise, water)
					)
				)
			)
        );
    }

    @Override
    public void addNetherSurfaceRules(HolderLookup<Biome> biomes, List<SurfaceRules.RuleSource> context) {
		context.add(
			SurfaceRules.state(Blocks.SPONGE.defaultBlockState())
		);
    }

    @Override
    public void addEndSurfaceRules(HolderLookup<Biome> biomes, List<SurfaceRules.RuleSource> context) {
		context.add(
			SurfaceRules.state(Blocks.BIRCH_LOG.defaultBlockState())
		);
    }

    @Override
    public void addGenericSurfaceRules(HolderLookup<Biome> biomes, List<FrozenDimensionBoundRuleSource> context) {
		context.add(new FrozenDimensionBoundRuleSource(
			Identifier.withDefaultNamespace("overworld"),
			SurfaceRules.sequence(
				SurfaceRules.ifTrue(
					SurfaceRules.isBiome(biomes, Biomes.BIRCH_FOREST),
					SurfaceRules.state(Blocks.HAY_BLOCK.defaultBlockState())
				)
			)
		));
		context.add(new FrozenDimensionBoundRuleSource(
			Identifier.withDefaultNamespace("the_nether"),
			SurfaceRules.sequence(
				SurfaceRules.ifTrue(
					SurfaceRules.isBiome(biomes, Biomes.WARPED_FOREST),
					SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState())
				)
			)
		));
    }
}
