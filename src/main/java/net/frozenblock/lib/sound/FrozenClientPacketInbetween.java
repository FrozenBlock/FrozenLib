package net.frozenblock.lib.sound;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FrozenClientPacketInbetween {

    public static void requestFrozenSoundSync(int id, ResourceKey<Level> level) {
        FrozenClientPacketToServer.sendFrozenSoundSyncRequest(id, level);
    }

}
