/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.testmod;

import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.frozenblock.datafixer.api.FabricDataFixerBuilder;
import net.fabricmc.frozenblock.datafixer.api.FabricDataFixes;
import net.fabricmc.frozenblock.datafixer.api.SimpleFixes;
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
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
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

        BlockScheduledTicks.TICKS.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos,
                        Blocks.BEDROCK.defaultBlockState(), 3));

		GravityAPI.MODIFICATIONS.register((ctx) -> {
			ctx.gravity = new Vec3(0.05, 0.8, 0.05);
		});

		GravityAPI.register(Level.OVERWORLD, new GravityBelt<>(300, 319, true, true, new AbsoluteGravityFunction(new Vec3(0.0, 0.1, 0.0))));
		assert GravityAPI.calculateGravity(Level.OVERWORLD, 300).y == 0.1;

		//GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityBelt<>(0, 192, new InterpolatedGravityFunction(0.1)));

		AdvancementEvents.INIT.register(holder -> {
			Advancement advancement = holder.value();
			switch (holder.id().toString()) {
				case "minecraft:story/mine_stone" -> {
          			AdvancementAPI.addLootTables(advancement, List.of(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY));
					advancement.rewards.experience = 100;
				}
				case "minecraft:story/upgrade_tools" -> {
					AdvancementAPI.addLootTables(advancement, List.of(id("test_loottable")));
					AdvancementAPI.addCriteria(
						advancement,
						"minecraft:plains",
						PlayerTrigger.TriggerInstance.located(
							LocationPredicate.inBiome(Biomes.PLAINS)
						)
					);
					AdvancementAPI.addRequirementsAsNewList(advancement, new String[][]{{"minecraft:plains"}});
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
		var builder = new FabricDataFixerBuilder(DATA_VERSION);
		builder.addSchema(0, FabricDataFixes.getBaseSchema());
		Schema schemaV1 = builder.addSchema(1, NamespacedSchema::new);
		SimpleFixes.addItemRenameFix(builder, "Rename camera namespace to frozenlib_testmod", FrozenSharedConstants.id("camera"), id("camera"), schemaV1);

		FabricDataFixes.buildAndRegisterFixer(mod, builder);
		LOGGER.info("DataFixes for FrozenLib Testmod have been applied");
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
