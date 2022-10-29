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

package net.frozenblock.lib.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.frozenblock.lib.item.api.FrozenCreativeTabs;
import net.frozenblock.lib.testmod.item.Camera;
import net.frozenblock.lib.testmod.item.LootTableWhacker;
import net.frozenblock.lib.replacements_and_lists.BlockScheduledTicks;
import net.frozenblock.lib.impl.BlockScheduledTicks;
import net.frozenblock.lib.testmod.config.ClothConfigInteractionHandler;
import net.frozenblock.lib.testmod.item.Camera;
import net.frozenblock.lib.testmod.item.LootTableWhacker;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleContext;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.frozenblock.lib.FrozenMain.id;

public final class FrozenTestMain implements ModInitializer, SurfaceRuleEvents.OverworldModifierCallback, SurfaceRuleEvents.NetherModifierCallback {

    public static final String MOD_ID = "frozenlib_testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean areConfigsInit = false;

    public static final Camera CAMERA = new Camera(new FabricItemSettings().maxCount(1));
    public static final LootTableWhacker LOOT_TABLE_WHACKER = new LootTableWhacker(new FabricItemSettings().maxCount(1));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, id("camera"), CAMERA);
        Registry.register(Registry.ITEM, id("loot_table_whacker"), LOOT_TABLE_WHACKER);
		FrozenCreativeTabs.add(CAMERA, CreativeModeTabs.TAB_TOOLS);
		FrozenCreativeTabs.add(LOOT_TABLE_WHACKER, CreativeModeTabs.TAB_TOOLS);

        BlockScheduledTicks.TICKS.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos,
                        Blocks.BEDROCK.defaultBlockState(), 3));
		if (ClothConfigInteractionHandler.testBoolean()) {

		}
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_1"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_2"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_3"), id("ancient_city/city_center/city_center_2"));
    }

	@Override
	public void modifyOverworldRules(SurfaceRuleContext.@NotNull Overworld context) {

	}

	@Override
	public void modifyNetherRules(SurfaceRuleContext.@NotNull Nether context) {

	}
}
