package net.frozenblock.lib;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoints.CommonEventEntrypoint;
import net.frozenblock.lib.events.FrozenEvents;
import net.frozenblock.lib.entrypoints.FrozenMainEntrypoint;
import net.frozenblock.lib.interfaces.EntityLoopingSoundInterface;
import net.frozenblock.lib.replacements_and_lists.BlockScheduledTicks;
import net.frozenblock.lib.sound.FrozenSoundPackets;
import net.frozenblock.lib.sound.FrozenSoundPredicates;
import net.frozenblock.lib.sound.MovingLoopingSoundEntityManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.util.List;

public final class FrozenMain implements ModInitializer {
    public static final String MOD_ID = "frozenblocklib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final NOPLogger LOGGER4 = NOPLogger.NOP_LOGGER;

    @Override
    public void onInitialize() {
        FrozenSoundPredicates.init();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            BlockScheduledTicks.ticks.put(Blocks.DIAMOND_BLOCK, (state, world, pos, random) -> world.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3));
            //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_1"), id("ancient_city/city_center/city_center_2"));
            //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_2"), id("ancient_city/city_center/city_center_2"));
            //StructurePoolElementIdReplacements.resourceLocationReplacements.put(new ResourceLocation("ancient_city/city_center/city_center_3"), id("ancient_city/city_center/city_center_2"));
            RegisterDev.init();
        }

        receiveSoundSyncPacket();

        FabricLoader.getInstance().getEntrypointContainers("frozenlib:main", FrozenMainEntrypoint.class).forEach(entrypoint -> {
            try {
                FrozenMainEntrypoint mainPoint = entrypoint.getEntrypoint();
                mainPoint.init();
                if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    mainPoint.initDevOnly();
                }
            } catch (Throwable ignored) {

            }
        });
    }

    //IDENTIFIERS
    public static final ResourceLocation FLYBY_SOUND_PACKET = id("flyby_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_LOOPING_SOUND_PACKET = id("moving_restriction_looping_sound_packet");
    public static final ResourceLocation STARTING_RESTRICTION_LOOPING_SOUND_PACKET = id("starting_moving_restriction_looping_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_SOUND_PACKET = id("moving_restriction_sound_packet");
    public static final ResourceLocation COOLDOWN_CHANGE_PACKET = id("cooldown_change_packet");
    public static final ResourceLocation REQUEST_LOOPING_SOUND_SYNC_PACKET = id("request_looping_sound_sync_packet");

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

    private static void receiveSoundSyncPacket() {
        ServerPlayNetworking.registerGlobalReceiver(FrozenMain.REQUEST_LOOPING_SOUND_SYNC_PACKET, (ctx, player, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            Level dimension = ctx.getLevel(byteBuf.readResourceKey(Registry.DIMENSION_REGISTRY));
            ctx.execute(() -> {
                if (dimension != null) {
                    Entity entity = dimension.getEntity(id);
                    if (entity != null) {
                        if (entity instanceof LivingEntity living) {
                            for (MovingLoopingSoundEntityManager.SoundLoopNBT nbt : ((EntityLoopingSoundInterface) living).getSounds().getSounds()) {
                                FrozenSoundPackets.createMovingRestrictionLoopingSound(player, entity, Registry.SOUND_EVENT.get(nbt.getSoundEventID()), SoundSource.valueOf(SoundSource.class, nbt.getOrdinal()), nbt.volume, nbt.pitch, nbt.restrictionID);
                            }
                        }
                    }
                }
            });
        });
    }

}
