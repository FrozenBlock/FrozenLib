package net.frozenblock.lib.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.frozenblock.lib.item.Camera;
import net.frozenblock.lib.item.LootTableWhacker;
import net.frozenblock.lib.events.ScheduledBlockEvents;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.frozenblock.lib.FrozenMain.id;

public final class FrozenTestMain implements ModInitializer {

    public static final String MOD_ID = "frozenblocklib_testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean areConfigsInit = false;

    public static final Camera CAMERA = new Camera(new FabricItemSettings().maxCount(1));
    public static final LootTableWhacker LOOT_TABLE_WHACKER = new LootTableWhacker(new FabricItemSettings().maxCount(1));

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
