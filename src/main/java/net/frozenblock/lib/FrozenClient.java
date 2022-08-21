package net.frozenblock.lib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.interfaces.CooldownInterface;
import net.frozenblock.lib.sound.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public final class FrozenClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(e -> {
            Minecraft client = Minecraft.getInstance();
            if (client.level != null) {
                FlyBySoundHub.update(client, client.player, true);
            }
        });

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            FlyBySoundHub.autoEntitiesAndSounds.put(EntityType.ARROW, new FlyBySoundHub.FlyBySound(1.0F, 1.0F, SoundSource.NEUTRAL, SoundEvents.AXE_SCRAPE));
        }

        receiveMovingLoopingSoundPacket();
        receiveMovingRestrictionSoundPacket();
        receiveMovingRestrictionLoopingSoundPacket();
        receiveFlybySoundPacket();
        receiveCooldownChangePacket();
    }

    private static void receiveMovingLoopingSoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_LOOPING_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
            SoundSource category = byteBuf.readEnum(SoundSource.class);
            float volume = byteBuf.readFloat();
            float pitch = byteBuf.readFloat();
            ResourceLocation predicateId = byteBuf.readResourceLocation();
            ctx.execute(() -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    Entity entity = world.getEntity(id);
                    if (entity != null) {
                        RegisterMovingSoundRestrictions.LoopPredicate<?> predicate = RegisterMovingSoundRestrictions.getPredicate(predicateId);
                        Minecraft.getInstance().getSoundManager().play(new MovingSoundLoop(entity, sound, category, volume, pitch, predicate));
                    }
                }
            });
        });
    }

    private static void receiveMovingRestrictionSoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
            SoundSource category = byteBuf.readEnum(SoundSource.class);
            float volume = byteBuf.readFloat();
            float pitch = byteBuf.readFloat();
            ResourceLocation predicateId = byteBuf.readResourceLocation();
            ctx.execute(() -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    Entity entity = world.getEntity(id);
                    if (entity != null) {
                        RegisterMovingSoundRestrictions.LoopPredicate<?> predicate = RegisterMovingSoundRestrictions.getPredicate(predicateId);
                        Minecraft.getInstance().getSoundManager().play(new MovingSoundWithRestriction(entity, sound, category, volume, pitch, predicate));
                    }
                }
            });
        });
    }

    private static void receiveMovingRestrictionLoopingSoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_LOOPING_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
            SoundSource category = byteBuf.readEnum(SoundSource.class);
            float volume = byteBuf.readFloat();
            float pitch = byteBuf.readFloat();
            ResourceLocation predicateId = byteBuf.readResourceLocation();
            ctx.execute(() -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    Entity entity = world.getEntity(id);
                if (entity != null) {
                    RegisterMovingSoundRestrictions.LoopPredicate<?> predicate = RegisterMovingSoundRestrictions.getPredicate(predicateId);
                    Minecraft.getInstance().getSoundManager().play(new MovingSoundLoopWithRestriction(entity, sound, category, volume, pitch, predicate));
                }
            }
            });
        });
    }

    private static void receiveFlybySoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.FLYBY_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
            SoundSource category = byteBuf.readEnum(SoundSource.class);
            float volume = byteBuf.readFloat();
            float pitch = byteBuf.readFloat();
            ctx.execute(() -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    Entity entity = world.getEntity(id);
                    if (entity != null) {
                        FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(pitch, volume, category, sound);
                        FlyBySoundHub.addEntity(entity, flyBySound);
                    }
                }
            });
        });
    }

    private static void receiveCooldownChangePacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.COOLDOWN_CHANGE_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            Item item = byteBuf.readById(Registry.ITEM);
            int additional = byteBuf.readVarInt();
            ctx.execute(() -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null && Minecraft.getInstance().player != null) {
                    ((CooldownInterface)Minecraft.getInstance().player.getCooldowns()).changeCooldown(item , additional);
                }
            });
        });
    }

}
