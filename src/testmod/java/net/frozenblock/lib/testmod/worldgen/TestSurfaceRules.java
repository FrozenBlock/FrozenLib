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
 */

package net.frozenblock.lib.testmod.worldgen;

import java.util.List;
import net.frozenblock.lib.worldgen.surface.api.FrozenDimensionBoundRuleSource;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class TestSurfaceRules implements SurfaceRuleEvents.OverworldSurfaceRuleCallback,
	SurfaceRuleEvents.NetherSurfaceRuleCallback, SurfaceRuleEvents.EndSurfaceRuleCallback,
	SurfaceRuleEvents.GenericSurfaceRuleCallback {
    @Override
    public void addOverworldSurfaceRules(List<SurfaceRules.RuleSource> context) {
        // When in doubt, T R A N S. Seed 7205143747332514273 is a good one for testing.
        SurfaceRules.ConditionSource blueNoise1 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.05, 0.1);
        SurfaceRules.ConditionSource pinkNoise1 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.1, 0.15);
        SurfaceRules.ConditionSource whiteNoise = SurfaceRules.noiseCondition(Noises.CALCITE, 0.15, 0.20);
        SurfaceRules.ConditionSource pinkNoise2 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.20, 0.25);
        SurfaceRules.ConditionSource blueNoise2 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.25, 0.30);

        SurfaceRules.RuleSource LIGHT_BLUE_CONCRETE = SurfaceRules.state(Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState());
        SurfaceRules.RuleSource PINK_CONCRETE = SurfaceRules.state(Blocks.PINK_CONCRETE.defaultBlockState());
        SurfaceRules.RuleSource WHITE_CONCRETE = SurfaceRules.state(Blocks.WHITE_CONCRETE.defaultBlockState());

        context.add(
                SurfaceRules.ifTrue(
                        SurfaceRules.abovePreliminarySurface(),
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(blueNoise1, LIGHT_BLUE_CONCRETE),
                                        SurfaceRules.ifTrue(pinkNoise1, PINK_CONCRETE),
                                        SurfaceRules.ifTrue(whiteNoise, WHITE_CONCRETE),
                                        SurfaceRules.ifTrue(pinkNoise2, PINK_CONCRETE),
                                        SurfaceRules.ifTrue(blueNoise2, LIGHT_BLUE_CONCRETE)
                                )
                        )
                )
        );
    }

    @Override
    public void addNetherSurfaceRules(List<SurfaceRules.RuleSource> context) {
		context.add(
			SurfaceRules.state(Blocks.SPONGE.defaultBlockState())
		);
    }

    @Override
    public void addEndSurfaceRules(List<SurfaceRules.RuleSource> context) {
		context.add(
			SurfaceRules.state(Blocks.BIRCH_LOG.defaultBlockState())
		);
    }

    @Override
    public void addGenericSurfaceRules(List<FrozenDimensionBoundRuleSource> context) {
		context.add(new FrozenDimensionBoundRuleSource(
			new ResourceLocation("overworld"),
			SurfaceRules.sequence(
				SurfaceRules.ifTrue(
					SurfaceRules.isBiome(Biomes.BIRCH_FOREST),
					SurfaceRules.state(Blocks.HAY_BLOCK.defaultBlockState())
				)
			)
		));
		context.add(new FrozenDimensionBoundRuleSource(
			new ResourceLocation("the_nether"),
			SurfaceRules.sequence(
				SurfaceRules.ifTrue(
					SurfaceRules.isBiome(Biomes.WARPED_FOREST),
					SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState())
				)
			)
		));
    }
}
