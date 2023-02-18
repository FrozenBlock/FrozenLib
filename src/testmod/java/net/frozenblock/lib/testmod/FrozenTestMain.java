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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.testmod.config.TestConfig;
import net.frozenblock.lib.tick.api.BlockScheduledTicks;
import net.frozenblock.lib.testmod.config.cloth.ClothConfigInteractionHandler;
import net.frozenblock.lib.testmod.item.Camera;
import net.frozenblock.lib.testmod.item.LootTableWhacker;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.frozenblock.lib.FrozenMain.id;

public final class FrozenTestMain implements ModInitializer {

    public static final String MOD_ID = "frozenlib_testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean areConfigsInit = false;

    public static final Camera CAMERA = new Camera(new FabricItemSettings().maxCount(1));
    public static final LootTableWhacker LOOT_TABLE_WHACKER = new LootTableWhacker(new FabricItemSettings().maxCount(1));

    @Override
    public void onInitialize() {
		LOGGER.info("The test toggle value is " + TestConfig.get().config().testToggle);
		LOGGER.info("The test vec3 value is " + TestConfig.get().config().typedVecList.value());
		SoundEvent sound = TestConfig.get().config().randomSound.value();
		LOGGER.info("The test soundevent value is " + sound + " and its ID is " + sound.getLocation());
        Registry.register(Registry.ITEM, id("camera"), CAMERA);
        Registry.register(Registry.ITEM, id("loot_table_whacker"), LOOT_TABLE_WHACKER);

        BlockScheduledTicks.TICKS.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos,
                        Blocks.BEDROCK.defaultBlockState(), 3));
		if (ClothConfigInteractionHandler.testBoolean()) {

		}
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_1"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_2"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_3"), id("ancient_city/city_center/city_center_2"));
    }
}
