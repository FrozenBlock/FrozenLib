package net.frozenblock.lib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoints.FrozenClientEntrypoint;
import net.frozenblock.lib.interfaces.CooldownInterface;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.frozenblock.lib.sound.*;
import net.frozenblock.lib.sound.distance_based.FadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.distance_based.MovingFadingDistanceSwitchingSoundLoop;
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
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client.ClientFreezer;

public final class FrozenClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientFreezer.onInitializeClient();
        ClientTickEvents.START_WORLD_TICK.register(e -> {
            Minecraft client = Minecraft.getInstance();
            if (client.level != null) {
                FlyBySoundHub.update(client, client.player, true);
            }
        });

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            FlyBySoundHub.autoEntitiesAndSounds.put(EntityType.ARROW, new FlyBySoundHub.FlyBySound(1.0F, 1.0F, SoundSource.NEUTRAL, SoundEvents.AXE_SCRAPE));
        }

        receiveMovingRestrictionSoundPacket();
        receiveMovingRestrictionLoopingSoundPacket();
        receiveStartingMovingRestrictionLoopingSoundPacket();
        receiveMovingRestrictionLoopingFadingDistanceSoundPacket();
        receiveFadingDistanceSoundPacket();
        receiveFlybySoundPacket();
        receiveCooldownChangePacket();

        FabricLoader.getInstance().getEntrypointContainers("frozenlib:client", FrozenClientEntrypoint.class).forEach(entrypoint -> {
            try {
                FrozenClientEntrypoint clientPoint = entrypoint.getEntrypoint();
                clientPoint.init();
                if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    clientPoint.initDevOnly();
                }
            } catch (Throwable ignored) {

            }
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
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null) {
                    Entity entity = level.getEntity(id);
                    if (entity != null) {
                        FrozenSoundPredicates.LoopPredicate<?> predicate = FrozenSoundPredicates.getPredicate(predicateId);
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
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null) {
                    Entity entity = level.getEntity(id);
                    if (entity != null) {
                        FrozenSoundPredicates.LoopPredicate<?> predicate = FrozenSoundPredicates.getPredicate(predicateId);
                        Minecraft.getInstance().getSoundManager().play(new MovingSoundLoopWithRestriction(entity, sound, category, volume, pitch, predicate));
                    }
                }
            });
        });
    }

    private static void receiveStartingMovingRestrictionLoopingSoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.STARTING_RESTRICTION_LOOPING_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            SoundEvent startingSound = byteBuf.readById(FrozenRegistry.STARTING_SOUND);
            SoundEvent loopingSound = byteBuf.readById(Registry.SOUND_EVENT);
            SoundSource category = byteBuf.readEnum(SoundSource.class);
            float volume = byteBuf.readFloat();
            float pitch = byteBuf.readFloat();
            ResourceLocation predicateId = byteBuf.readResourceLocation();
            ctx.execute(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null) {
                    Entity entity = level.getEntity(id);
                    if (entity != null) {
                        FrozenSoundPredicates.LoopPredicate<?> predicate = FrozenSoundPredicates.getPredicate(predicateId);
                        Minecraft.getInstance().getSoundManager().play(new StartingSoundInstance(entity, startingSound, loopingSound, category, volume, pitch, predicate, new MovingSoundLoopWithRestriction(entity, loopingSound, category, volume, pitch, predicate)));
                    }
                }
            });
        });
    }

    private static void receiveMovingRestrictionLoopingFadingDistanceSoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
            SoundEvent sound2 = byteBuf.readById(Registry.SOUND_EVENT);
            SoundSource category = byteBuf.readEnum(SoundSource.class);
            float volume = byteBuf.readFloat();
            float pitch = byteBuf.readFloat();
            float fadeDist = byteBuf.readFloat();
            float maxDist = byteBuf.readFloat();
            ResourceLocation predicateId = byteBuf.readResourceLocation();
            ctx.execute(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null) {
                    Entity entity = level.getEntity(id);
                    if (entity != null) {
                        FrozenSoundPredicates.LoopPredicate<?> predicate = FrozenSoundPredicates.getPredicate(predicateId);
                        Minecraft.getInstance().getSoundManager().play(new MovingFadingDistanceSwitchingSoundLoop(entity, sound, category, volume, pitch, predicate, fadeDist, maxDist, volume, false));
                        Minecraft.getInstance().getSoundManager().play(new MovingFadingDistanceSwitchingSoundLoop(entity, sound2, category, volume, pitch, predicate, fadeDist, maxDist, volume, true));
                    }
                }
            });
        });
    }

    private static void receiveFadingDistanceSoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(FrozenMain.FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();
            SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
            SoundEvent sound2 = byteBuf.readById(Registry.SOUND_EVENT);
            SoundSource category = byteBuf.readEnum(SoundSource.class);
            float volume = byteBuf.readFloat();
            float pitch = byteBuf.readFloat();
            float fadeDist = byteBuf.readFloat();
            float maxDist = byteBuf.readFloat();
            ctx.execute(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null) {
                    Minecraft.getInstance().getSoundManager().play(new FadingDistanceSwitchingSound(sound, category, volume, pitch, fadeDist, maxDist, volume, false, x, y, z));
                    Minecraft.getInstance().getSoundManager().play(new FadingDistanceSwitchingSound(sound2, category, volume, pitch, fadeDist, maxDist, volume, true, x ,y ,z));
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
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null) {
                    Entity entity = level.getEntity(id);
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
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null && Minecraft.getInstance().player != null) {
                    ((CooldownInterface)Minecraft.getInstance().player.getCooldowns()).changeCooldown(item , additional);
                }
            });
        });
    }

}
