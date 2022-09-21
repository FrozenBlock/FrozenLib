package net.frozenblock.lib.sound;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.interfaces.EntityLoopingSoundInterface;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FrozenSoundPackets {

    public static void createMovingRestrictionSound(Level world, Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation id) {
        if (!world.isClientSide) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            byteBuf.writeVarInt(entity.getId());
            byteBuf.writeId(Registry.SOUND_EVENT, sound);
            byteBuf.writeEnum(category);
            byteBuf.writeFloat(volume);
            byteBuf.writeFloat(pitch);
            byteBuf.writeResourceLocation(id);
            for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) world, entity.blockPosition())) {
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
            for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) world, entity.blockPosition())) {
                ServerPlayNetworking.send(player, FrozenMain.MOVING_RESTRICTION_LOOPING_SOUND_PACKET, byteBuf);
            }
            if (entity instanceof LivingEntity living) {
                ((EntityLoopingSoundInterface)living).addSound(Registry.SOUND_EVENT.getKey(sound), category, volume, pitch, id);
            }
        }
    }

    public static void createMovingRestrictionLoopingSound(ServerPlayer player, Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation id) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(entity.getId());
        byteBuf.writeId(Registry.SOUND_EVENT, sound);
        byteBuf.writeEnum(category);
        byteBuf.writeFloat(volume);
        byteBuf.writeFloat(pitch);
        byteBuf.writeResourceLocation(id);
        ServerPlayNetworking.send(player, FrozenMain.MOVING_RESTRICTION_LOOPING_SOUND_PACKET, byteBuf);
    }

    public static void createStartingMovingRestrictionLoopingSound(Level world, Entity entity, SoundEvent startingSound, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation id) {
        if (!world.isClientSide) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            byteBuf.writeVarInt(entity.getId());
            byteBuf.writeId(FrozenRegistry.STARTING_SOUND, startingSound);
            byteBuf.writeId(Registry.SOUND_EVENT, sound);
            byteBuf.writeEnum(category);
            byteBuf.writeFloat(volume);
            byteBuf.writeFloat(pitch);
            byteBuf.writeResourceLocation(id);
            for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) world, entity.blockPosition())) {
                ServerPlayNetworking.send(player, FrozenMain.STARTING_RESTRICTION_LOOPING_SOUND_PACKET, byteBuf);
            }
            if (entity instanceof LivingEntity living) {
                ((EntityLoopingSoundInterface)living).addSound(Registry.SOUND_EVENT.getKey(sound), category, volume, pitch, id);
            }
        }
    }

    public static void createStartingMovingRestrictionLoopingSound(ServerPlayer player, Entity entity, SoundEvent startingSound, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation id) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(entity.getId());
        byteBuf.writeId(FrozenRegistry.STARTING_SOUND, startingSound);
        byteBuf.writeId(Registry.SOUND_EVENT, sound);
        byteBuf.writeEnum(category);
        byteBuf.writeFloat(volume);
        byteBuf.writeFloat(pitch);
        byteBuf.writeResourceLocation(id);
        ServerPlayNetworking.send(player, FrozenMain.STARTING_RESTRICTION_LOOPING_SOUND_PACKET, byteBuf);
    }

}
