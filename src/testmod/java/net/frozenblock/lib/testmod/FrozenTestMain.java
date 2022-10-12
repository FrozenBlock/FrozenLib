package net.frozenblock.lib.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.frozenblock.lib.item.Camera;
import net.frozenblock.lib.item.LootTableWhacker;
import net.frozenblock.lib.replacements_and_lists.BlockScheduledTicks;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Blocks;

import static net.frozenblock.lib.FrozenMain.id;

public final class FrozenTestMain implements ModInitializer {

    public static final Camera CAMERA = new Camera(new FabricItemSettings());
    public static final LootTableWhacker LOOT_TABLE_WHACKER =
            new LootTableWhacker(new FabricItemSettings());

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, id("camera"), CAMERA);
        Registry.register(Registry.ITEM, id("loot_table_whacker"), LOOT_TABLE_WHACKER);

        BlockScheduledTicks.ticks.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos,
                        Blocks.BEDROCK.defaultBlockState(), 3));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_1"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_2"), id("ancient_city/city_center/city_center_2"));
        //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_3"), id("ancient_city/city_center/city_center_2"));
    }
}
