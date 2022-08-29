package net.frozenblock.lib.sound;

public class FrozenClientPacketInbetween {

    public static void requestFrozenSoundSync(int id) {
        FrozenClientPacketToServer.sendFrozenSoundSyncRequest(id);
    }

}
