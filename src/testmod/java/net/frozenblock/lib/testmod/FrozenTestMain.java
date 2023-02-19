/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.testmod;

import com.mojang.datafixers.schemas.Schema;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.testmod.config.TestConfig;
import net.frozenblock.lib.tick.api.BlockScheduledTicks;
import net.frozenblock.lib.item.api.FrozenCreativeTabs;
import net.frozenblock.lib.testmod.config.cloth.ClothConfigInteractionHandler;
import net.frozenblock.lib.testmod.item.Camera;
import net.frozenblock.lib.testmod.item.LootTableWhacker;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixerBuilder;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixes;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.SimpleFixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FrozenTestMain implements ModInitializer {

    public static final String MOD_ID = "frozenlib_testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean areConfigsInit = false;

    public static final Camera CAMERA = new Camera(new FabricItemSettings().maxCount(1).requiredFeatures(FeatureFlags.VANILLA));
    public static final LootTableWhacker LOOT_TABLE_WHACKER = new LootTableWhacker(new FabricItemSettings().maxCount(1).requiredFeatures(FeatureFlags.VANILLA));

    @Override
    public void onInitialize() {
		applyDataFixes(FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow());
		LOGGER.info("The test toggle value is " + TestConfig.get().config().testToggle);
		LOGGER.info("The test vec3 value is " + TestConfig.get().config().typedVecList.value());
		SoundEvent sound = TestConfig.get().config().randomSound.value();
		LOGGER.info("The test soundevent value is " + sound + " and its ID is " + sound.getLocation());
        Registry.register(BuiltInRegistries.ITEM, id("camera"), CAMERA);
        Registry.register(BuiltInRegistries.ITEM, id("loot_table_whacker"), LOOT_TABLE_WHACKER);

		FrozenCreativeTabs.add(CAMERA, CreativeModeTabs.OP_BLOCKS);
		FrozenCreativeTabs.add(LOOT_TABLE_WHACKER, CreativeModeTabs.OP_BLOCKS);

        BlockScheduledTicks.TICKS.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos,
                        Blocks.BEDROCK.defaultBlockState(), 3));
		if (ClothConfigInteractionHandler.testBoolean()) {

		}
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
		SimpleFixes.addItemRenameFix(builder, "Rename camera namespace to frozenlib_testmod", FrozenMain.id("camera"), id("camera"), schemaV1);

		QuiltDataFixes.buildAndRegisterFixer(mod, builder);
		LOGGER.info("DataFixes for FrozenLib Testmod have been applied");
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
