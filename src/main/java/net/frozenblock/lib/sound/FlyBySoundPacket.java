package net.frozenblock.lib.sound;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class FlyBySoundPacket {

    public static void createFlybySound(Level world, Entity entity,
                                        SoundEvent sound, SoundSource category,
                                        float volume, float pitch) {
        if (!world.isClientSide) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            byteBuf.writeVarInt(entity.getId());
            byteBuf.writeId(Registry.SOUND_EVENT, sound);
            byteBuf.writeEnum(category);
            byteBuf.writeFloat(volume);
            byteBuf.writeFloat(pitch);
            for (ServerPlayer player : PlayerLookup.around((ServerLevel) world,
                    entity.blockPosition(), 128)) {
                ServerPlayNetworking.send(player, FrozenMain.FLYBY_SOUND_PACKET,
                        byteBuf);
            }
        }
    }

}
