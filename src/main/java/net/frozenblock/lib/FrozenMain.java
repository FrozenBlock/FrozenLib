package net.frozenblock.lib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.replacements_and_lists.BlockScheduledTicks;
import net.frozenblock.lib.sound.RegisterMovingSoundRestrictions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.logging.Logger;

public final class FrozenMain implements ModInitializer {
    public static final String MOD_ID = "frozenblocklib";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        RegisterMovingSoundRestrictions.init();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            BlockScheduledTicks.ticks.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3));
            //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_1"), id("ancient_city/city_center/city_center_2"));
            //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_2"), id("ancient_city/city_center/city_center_2"));
            //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_3"), id("ancient_city/city_center/city_center_2"));
            RegisterDev.init();
        }

    }

    //IDENTIFIERS
    public static final ResourceLocation FLYBY_SOUND_PACKET = id("flyby_sound_packet");
    public static final ResourceLocation MOVING_LOOPING_SOUND_PACKET = id("moving_looping_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_LOOPING_SOUND_PACKET = id("moving_restriction_looping_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_SOUND_PACKET = id("moving_restriction_sound_packet");
    public static final ResourceLocation COOLDOWN_CHANGE_PACKET = id("cooldown_change_packet");

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static String string(String path) {
        return id(path).toString();
    }

    public static void log(String string, boolean should) {
        if (should) {
            LOGGER.info(string);
        }
    }

}
