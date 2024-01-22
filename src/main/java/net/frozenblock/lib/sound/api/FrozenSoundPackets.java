/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.api;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.sound.api.networking.FadingDistanceSwitchingSoundPacket;
import net.frozenblock.lib.sound.api.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.api.networking.MovingFadingDistanceSwitchingRestrictionSoundPacket;
import net.frozenblock.lib.sound.api.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.api.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public final class FrozenSoundPackets {
	private FrozenSoundPackets() {
		throw new UnsupportedOperationException("FrozenSoundPackets contains only static declarations.");
	}

	public static void createLocalSound(@NotNull Level level, BlockPos pos, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		if (!level.isClientSide) {
			var packet = new LocalSoundPacket(Vec3.atCenterOf(pos), sound, source, volume, pitch, distanceDelay);
			for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) level, pos)) {
				ServerPlayNetworking.send(player, packet);
			}
		}
	}

	public static void createLocalSound(@NotNull Level level, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		if (!level.isClientSide) {
			var packet = new LocalSoundPacket(new Vec3(x, y, z), sound, source, volume, pitch, distanceDelay);
			for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) level, BlockPos.containing(x, y, z))) {
				ServerPlayNetworking.send(player, packet);
			}
		}
	}

	public static void createFlybySound(@NotNull Level world, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch) {
		if (!world.isClientSide) {
			var packet = new FlyBySoundPacket(entity.getId(), sound, category, volume, pitch);
			for (ServerPlayer player : PlayerLookup.tracking(entity)) {
				ServerPlayNetworking.send(player, packet);
			}
			if (entity instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, packet);
			}
		}
	}

    public static void createMovingRestrictionSound(@NotNull Level world, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, ResourceLocation predicate, boolean stopOnDeath) {
        if (!world.isClientSide) {
            var packet = new MovingRestrictionSoundPacket(entity.getId(), sound, category, volume, pitch, predicate, stopOnDeath, false);
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(player, packet);
            }
			if (entity instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, packet);
			}
        }
    }

    public static void createMovingRestrictionLoopingSound(@NotNull Level world, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, ResourceLocation predicate, boolean stopOnDeath) {
        if (!world.isClientSide) {
			var packet = new MovingRestrictionSoundPacket(entity.getId(), sound, category, volume, pitch, predicate, stopOnDeath, true);
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(player, packet);
            }
			if (entity instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, packet);
			}
			((EntityLoopingSoundInterface)entity).addSound(sound.unwrapKey().orElseThrow().location(), category, volume, pitch, predicate, stopOnDeath);
        }
    }

    public static void createMovingRestrictionLoopingSound(ServerPlayer player, @NotNull Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, ResourceLocation id, boolean stopOnDeath) {
        var packet = new MovingRestrictionSoundPacket(entity.getId(), sound, category, volume, pitch, id, stopOnDeath, true);
		ServerPlayNetworking.send(player, packet);
    }

    public static void createMovingRestrictionLoopingFadingDistanceSound(@NotNull Level level, Entity entity, Holder<SoundEvent> sound, Holder<SoundEvent> sound2, SoundSource category, float volume, float pitch, ResourceLocation predicate, boolean stopOnDeath, float fadeDist, float maxDist) {
        if (!level.isClientSide) {
			CustomPacketPayload packet = new MovingFadingDistanceSwitchingRestrictionSoundPacket(
				entity.getId(),
				sound,
				sound2,
				category,
				volume,
				pitch,
				fadeDist,
				maxDist,
				predicate,
				stopOnDeath,
				true
			);
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(player, packet);
            }
			if (entity instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, packet);
			}
			((EntityLoopingFadingDistanceSoundInterface)entity).addFadingDistanceSound(sound.unwrapKey().orElseThrow().location(), sound2.unwrapKey().orElseThrow().location(), category, volume, pitch, predicate, stopOnDeath, fadeDist, maxDist);
        }
    }

    public static void createMovingRestrictionLoopingFadingDistanceSound(ServerPlayer player, @NotNull Entity entity, Holder<SoundEvent> sound, Holder<SoundEvent> sound2, SoundSource category, float volume, float pitch, ResourceLocation predicate, boolean stopOnDeath, float fadeDist, float maxDist) {
		CustomPacketPayload packet = new MovingFadingDistanceSwitchingRestrictionSoundPacket(
			entity.getId(),
			sound,
			sound2,
			category,
			volume,
			pitch,
			fadeDist,
			maxDist,
			predicate,
			stopOnDeath,
			true
		);
        ServerPlayNetworking.send(player, packet);
    }

    public static void createMovingRestrictionFadingDistanceSound(ServerPlayer player, @NotNull Entity entity, Holder<SoundEvent> sound, Holder<SoundEvent> sound2, SoundSource category, float volume, float pitch, ResourceLocation predicate, boolean stopOnDeath, float fadeDist, float maxDist) {
        CustomPacketPayload packet = new MovingFadingDistanceSwitchingRestrictionSoundPacket(
			entity.getId(),
			sound,
			sound2,
			category,
			volume,
			pitch,
			fadeDist,
			maxDist,
			predicate,
			stopOnDeath,
			false
		);
        ServerPlayNetworking.send(player, packet);
    }

    public static void createFadingDistanceSound(@NotNull Level level, Vec3 pos, Holder<SoundEvent> sound, Holder<SoundEvent> sound2, SoundSource category, float volume, float pitch, float fadeDist, float maxDist) {
        if (!level.isClientSide) {
			CustomPacketPayload packet = new FadingDistanceSwitchingSoundPacket(
				pos,
				sound,
				sound2,
				category,
				volume,
				pitch,
				fadeDist,
				maxDist
			);
            for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) level, BlockPos.containing(pos))) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    public static void createStartingMovingRestrictionLoopingSound(@NotNull Level world, Entity entity, Holder<SoundEvent> startingSound, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, ResourceLocation predicate, boolean stopOnDeath) {
        if (!world.isClientSide) {
            var packet = new StartingMovingRestrictionSoundLoopPacket(entity.getId(), startingSound, sound, category, volume, pitch, predicate, stopOnDeath);
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(player, packet);
            }
			if (entity instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, packet);
			}
			((EntityLoopingSoundInterface)entity).addSound(sound.unwrapKey().orElseThrow().location(), category, volume, pitch, predicate, stopOnDeath);
        }
    }

    public static void createStartingMovingRestrictionLoopingSound(ServerPlayer player, @NotNull Entity entity, Holder<SoundEvent> startingSound, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, ResourceLocation predicate, boolean stopOnDeath) {
        ServerPlayNetworking.send(player, new StartingMovingRestrictionSoundLoopPacket(entity.getId(), startingSound, sound, category, volume, pitch, predicate, stopOnDeath));
    }

	public static void createLocalPlayerSound(ServerPlayer player, Holder<SoundEvent> sound, float volume, float pitch) {
		var packet = new LocalPlayerSoundPacket(sound, volume, pitch);
		ServerPlayNetworking.send(player, packet);
	}

}
