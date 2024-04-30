/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.testmod;

import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.advancement.api.AdvancementAPI;
import net.frozenblock.lib.advancement.api.AdvancementEvents;
import net.frozenblock.lib.block.api.tick.BlockScheduledTicks;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.frozenblock.lib.gravity.api.GravityBelt;
import net.frozenblock.lib.gravity.api.functions.AbsoluteGravityFunction;
import net.frozenblock.lib.testmod.config.TestConfig;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixerBuilder;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixes;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.SimpleFixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FrozenTestMain implements ModInitializer {

    public static final String MOD_ID = "frozenlib_testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
		applyDataFixes(FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow());
        LOGGER.info("The test toggle value is {}", TestConfig.get().testToggle);
        LOGGER.info("The test vec3 value is {}", TestConfig.get().typedVecList);
		Holder<SoundEvent> sound = TestConfig.get().randomSound.value();
        LOGGER.info("The test soundevent value is {} and its ID is {}", sound, sound.unwrapKey().orElseThrow().location());

        BlockScheduledTicks.TICKS.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos,
                        Blocks.BEDROCK.defaultBlockState(), 3));

		GravityAPI.MODIFICATIONS.register((ctx) -> {
			ctx.gravity = new Vec3(0.05, 0.8, 0.05);
		});

		GravityAPI.register(Level.OVERWORLD, new GravityBelt<>(300, 319, true, true, new AbsoluteGravityFunction(new Vec3(0.0, 0.1, 0.0))));
		assert GravityAPI.calculateGravity(Level.OVERWORLD, 300).y == 0.1;

		//GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityBelt<>(0, 192, new InterpolatedGravityFunction(0.1)));

		AdvancementEvents.INIT.register((holder, registries) -> {
			Advancement advancement = holder.value();
			switch (holder.id().toString()) {
				case "minecraft:story/mine_stone" -> {
          AdvancementAPI.addLootTables(advancement, List.of(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY));
					advancement.rewards.experience = 100;
				}
				case "minecraft:story/upgrade_tools" -> {
					AdvancementAPI.addLootTables(advancement, List.of(ResourceKey.create(Registries.LOOT_TABLE, id("test_loottable"))));
					AdvancementAPI.addCriteria(
						advancement,
						"minecraft:plains",
						PlayerTrigger.TriggerInstance.located(
							LocationPredicate.Builder.inBiome(registries.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS))
						)
					);
					AdvancementAPI.addRequirementsAsNewList(advancement, AdvancementRequirements.anyOf(List.of("minecraft:plains")));
					advancement.rewards.experience = 1000;
				}
				default -> {}
			}
		});
		//StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_1"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_2"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_3"), id("ancient_city/city_center/city_center_2"));
    }

	private static final int DATA_VERSION = 1;

	private static void applyDataFixes(ModContainer mod) {
		LOGGER.info("Applying DataFixes for FrozenLib Testmod");
		var builder = new QuiltDataFixerBuilder(DATA_VERSION);
		builder.addSchema(0, QuiltDataFixes.BASE_SCHEMA);
		Schema schemaV1 = builder.addSchema(1, NamespacedSchema::new);
		SimpleFixes.addItemRenameFix(builder, "Rename camera namespace to frozenlib_testmod", FrozenSharedConstants.id("camera"), id("camera"), schemaV1);

		QuiltDataFixes.buildAndRegisterFixer(mod, builder);
		LOGGER.info("DataFixes for FrozenLib Testmod have been applied");
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
