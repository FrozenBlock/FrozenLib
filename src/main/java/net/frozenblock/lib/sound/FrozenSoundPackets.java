package net.frozenblock.lib.sound;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class FrozenSoundPackets {

    public static void createMovingLoopingSound(Level world, Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation id) {
        if (!world.isClientSide) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            byteBuf.writeVarInt(entity.getId());
            byteBuf.writeId(Registry.SOUND_EVENT, sound);
            byteBuf.writeEnum(category);
            byteBuf.writeFloat(volume);
            byteBuf.writeFloat(pitch);
            byteBuf.writeResourceLocation(id);
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(player, FrozenMain.MOVING_LOOPING_SOUND_PACKET, byteBuf);
            }
        }
    }

    public static void createMovingRestrictionSound(Level world, Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation id) {
        if (!world.isClientSide) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            byteBuf.writeVarInt(entity.getId());
            byteBuf.writeId(Registry.SOUND_EVENT, sound);
            byteBuf.writeEnum(category);
            byteBuf.writeFloat(volume);
            byteBuf.writeFloat(pitch);
            byteBuf.writeResourceLocation(id);
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(player, FrozenMain.MOVING_RESTRICTION_SOUND_PACKET, byteBuf);
            }
        }
    }

    public static void createMovingRestrictionLoopingSound(Level world, Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation id) {
        if (!world.isClientSide) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            byteBuf.writeVarInt(entity.getId());
            byteBuf.writeId(Registry.SOUND_EVENT, sound);
            byteBuf.writeEnum(category);
            byteBuf.writeFloat(volume);
            byteBuf.writeFloat(pitch);
            byteBuf.writeResourceLocation(id);
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(player, FrozenMain.MOVING_RESTRICTION_LOOPING_SOUND_PACKET, byteBuf);
            }
        }
    }

}
