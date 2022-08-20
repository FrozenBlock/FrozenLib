package net.frozenblock.lib;

import net.fabricmc.api.ModInitializer;
import net.frozenblock.lib.sound.RegisterMovingSoundRestrictions;
import net.minecraft.resources.ResourceLocation;

public final class FrozenMain implements ModInitializer {
    public static final String MOD_ID = "frozenblocklib";

    @Override
    public void onInitialize() {
        RegisterMovingSoundRestrictions.init();
    }

    //IDENTIFIERS
    public static final ResourceLocation FLYBY_SOUND_PACKET = id("flyby_sound_packet");
    public static final ResourceLocation MOVING_LOOPING_SOUND_PACKET = id("moving_looping_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_LOOPING_SOUND_PACKET = id("moving_restriction_looping_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_SOUND_PACKET = id("moving_restriction_sound_packet");

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static String string(String path) {
        return id(path).toString();
    }

}
