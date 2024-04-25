/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.sound.api;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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
