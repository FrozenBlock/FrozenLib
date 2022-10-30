/*
 * Copyright 2022 QuiltMC
 * Copyright 2022 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.test;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleContext;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleEvents;

public class QuiltSurfaceRuleTest implements SurfaceRuleEvents.OverworldModifierCallback,
		SurfaceRuleEvents.NetherModifierCallback,
		SurfaceRuleEvents.TheEndModifierCallback {
	@Override
	public void modifyOverworldRules(@NotNull SurfaceRuleContext.Overworld context) {
		// When in doubt, T R A N S. Seed 7205143747332514273 is a good one for testing.
		SurfaceRules.ConditionSource blueNoise1 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.05, 0.1);
		SurfaceRules.ConditionSource pinkNoise1 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.1, 0.15);
		SurfaceRules.ConditionSource whiteNoise = SurfaceRules.noiseCondition(Noises.CALCITE, 0.15, 0.20);
		SurfaceRules.ConditionSource pinkNoise2 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.20, 0.25);
		SurfaceRules.ConditionSource blueNoise2 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.25, 0.30);

		SurfaceRules.RuleSource LIGHT_BLUE_CONCRETE = SurfaceRules.state(Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource PINK_CONCRETE = SurfaceRules.state(Blocks.PINK_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource WHITE_CONCRETE = SurfaceRules.state(Blocks.WHITE_CONCRETE.defaultBlockState());

		context.ruleSources().add(0,
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
	public void modifyNetherRules(@NotNull SurfaceRuleContext.Nether context) {
		SurfaceRules.ConditionSource redNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.04, -0.08);
		SurfaceRules.ConditionSource orangeNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.8, -0.12);
		SurfaceRules.ConditionSource yellowNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.12, -0.16);
		SurfaceRules.ConditionSource greenNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.16, -0.20);
		SurfaceRules.ConditionSource lightBlueNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.20, -0.24);
		SurfaceRules.ConditionSource darkBlueNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.24, -0.28);
		SurfaceRules.ConditionSource purpleNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, -0.28, -0.32);

		SurfaceRules.RuleSource RED_CONCRETE = SurfaceRules.state(Blocks.RED_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource ORANGE_CONCRETE = SurfaceRules.state(Blocks.ORANGE_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource YELLOW_CONCRETE = SurfaceRules.state(Blocks.YELLOW_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource GREEN_CONCRETE = SurfaceRules.state(Blocks.GREEN_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource LIGHT_BLUE_CONCRETE = SurfaceRules.state(Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource DARK_BLUE_CONCRETE = SurfaceRules.state(Blocks.BLUE_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource PURPLE_CONCRETE = SurfaceRules.state(Blocks.PURPLE_CONCRETE.defaultBlockState());

		context.ruleSources().add(0,
				SurfaceRules.ifTrue(
						SurfaceRules.UNDER_CEILING,
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(redNoise, RED_CONCRETE),
								SurfaceRules.ifTrue(orangeNoise, ORANGE_CONCRETE),
								SurfaceRules.ifTrue(yellowNoise, YELLOW_CONCRETE),
								SurfaceRules.ifTrue(greenNoise, GREEN_CONCRETE),
								SurfaceRules.ifTrue(lightBlueNoise, LIGHT_BLUE_CONCRETE),
								SurfaceRules.ifTrue(darkBlueNoise, DARK_BLUE_CONCRETE),
								SurfaceRules.ifTrue(purpleNoise, PURPLE_CONCRETE)
						)
				)
		);
	}

	@Override
	public void modifyTheEndRules(@NotNull SurfaceRuleContext.TheEnd context) {
		ResourceKey<Biome> TEST_END_HIGHLANDS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("quilt_biome_testmod", "test_end_highlands"));
		ResourceKey<Biome> TEST_END_MIDLANDS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("quilt_biome_testmod", "test_end_midlands"));
		ResourceKey<Biome> TEST_END_BARRRENS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("quilt_biome_testmod", "test_end_barrens"));

		SurfaceRules.ConditionSource pinkBiome = SurfaceRules.isBiome(TEST_END_HIGHLANDS);
		SurfaceRules.ConditionSource whiteBiome = SurfaceRules.isBiome(TEST_END_MIDLANDS);
		SurfaceRules.ConditionSource purpleBiome = SurfaceRules.isBiome(TEST_END_BARRRENS);
		SurfaceRules.ConditionSource blackBiome = SurfaceRules.isBiome(Biomes.END_BARRENS);
		SurfaceRules.ConditionSource blueBiome = SurfaceRules.isBiome(Biomes.THE_END);

		SurfaceRules.RuleSource PINK_CONCRETE = SurfaceRules.state(Blocks.PINK_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource WHITE_CONCRETE = SurfaceRules.state(Blocks.WHITE_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource PURPLE_CONCRETE = SurfaceRules.state(Blocks.PURPLE_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource BLACK_CONCRETE = SurfaceRules.state(Blocks.BLACK_CONCRETE.defaultBlockState());
		SurfaceRules.RuleSource BLUE_CONCRETE = SurfaceRules.state(Blocks.BLUE_CONCRETE.defaultBlockState());

		// genderfluEND :D
		context.ruleSources().add(0,
				SurfaceRules.sequence(
						SurfaceRules.ifTrue(pinkBiome, PINK_CONCRETE),
						SurfaceRules.ifTrue(whiteBiome, WHITE_CONCRETE),
						SurfaceRules.ifTrue(purpleBiome, PURPLE_CONCRETE),
						SurfaceRules.ifTrue(blackBiome, BLACK_CONCRETE),
						SurfaceRules.ifTrue(blueBiome, BLUE_CONCRETE)
				)
		);
	}
}
