/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.impl.networking;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@UtilityClass
public class FrozenLibSoundPackets {

	public static void createAndSendLocalSound(
		Level level,
		BlockPos pos,
		Holder<SoundEvent> sound,
		SoundSource source,
		float volume,
		float pitch,
		boolean distanceDelay
	) {
		createAndSendLocalSound(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, source, volume, pitch, distanceDelay);
	}

	public static void createAndSendLocalSound(
		Level level,
		double x,
		double y,
		double z,
		Holder<SoundEvent> sound,
		SoundSource source,
		float volume,
		float pitch,
		boolean distanceDelay
	) {
		if (!(level instanceof ServerLevel serverLevel)) return;
		for (ServerPlayer player : PlayerLookup.tracking(serverLevel, BlockPos.containing(x, y, z))) {
			ServerPlayNetworking.send(
				player,
				new LocalSoundPacket(new Vec3(x, y, z), sound, source, volume, pitch, distanceDelay)
			);
		}
	}

	public static void createAndSendRelativeMovingSound(
		Level level,
		BlockPos pos,
		Holder<SoundEvent> sound,
		SoundSource source,
		float volume,
		float pitch
	) {
		if (!(level instanceof ServerLevel serverLevel)) return;
		for (ServerPlayer player : PlayerLookup.tracking(serverLevel, pos)) {
			ServerPlayNetworking.send(
				player,
				new RelativeMovingSoundPacket(pos, sound, source, volume, pitch)
			);
		}
	}

	public static void createAndSendFlybySound(
		Level level,
		Entity entity,
		Holder<SoundEvent> sound,
		SoundSource category,
		float volume,
		float pitch
	) {
		if (!(level instanceof ServerLevel)) return;

		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			ServerPlayNetworking.send(
				player,
				new FlyBySoundPacket(entity.getId(), sound, category, volume, pitch)
			);
		}

		if (entity instanceof ServerPlayer player) {
			ServerPlayNetworking.send(
				player,
				new FlyBySoundPacket(entity.getId(), sound, category, volume, pitch)
			);
		}
	}

    public static void createAndSendMovingRestrictionSound(
		Level level,
		Entity entity,
		Holder<SoundEvent> sound,
		SoundSource category,
		float volume,
		float pitch,
		Identifier predicate,
		boolean stopOnDeath
	) {
		if (!(level instanceof ServerLevel)) return;

		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			ServerPlayNetworking.send(
				player,
				new MovingRestrictionSoundPacket(
					entity.getId(),
					sound,
					category,
					volume,
					pitch,
					predicate,
					stopOnDeath,
					false
				)
			);
		}

		if (entity instanceof ServerPlayer player) {
			ServerPlayNetworking.send(
				player,
				new MovingRestrictionSoundPacket(
					entity.getId(),
					sound,
					category,
					volume,
					pitch,
					predicate,
					stopOnDeath,
					false
				)
			);
		}
    }

    public static void createAndSendMovingRestrictionLoopingSound(
		Level level,
		Entity entity,
		Holder<SoundEvent> sound,
		SoundSource category,
		float volume,
		float pitch,
		Identifier predicate,
		boolean stopOnDeath
	) {
		if (!(level instanceof ServerLevel) || !(entity instanceof EntityLoopingSoundInterface soundInterface)) return;

		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			ServerPlayNetworking.send(
				player,
				new MovingRestrictionSoundPacket(
					entity.getId(),
					sound,
					category,
					volume,
					pitch,
					predicate,
					stopOnDeath,
					true
				)
			);
			soundInterface.frozenLib$addSound(sound.unwrapKey().orElseThrow().identifier(), category, volume, pitch, predicate, stopOnDeath);
		}

		if (entity instanceof ServerPlayer player) {
			ServerPlayNetworking.send(
				player,
				new MovingRestrictionSoundPacket(
					entity.getId(),
					sound,
					category,
					volume,
					pitch,
					predicate,
					stopOnDeath,
					true
				)
			);
		}
    }

    public static void createAndSendMovingRestrictionLoopingSound(
		ServerPlayer player,
		Entity entity,
		Holder<SoundEvent> sound,
		SoundSource category,
		float volume,
		float pitch,
		Identifier id,
		boolean stopOnDeath
	) {
		ServerPlayNetworking.send(
			player,
			new MovingRestrictionSoundPacket(
				entity.getId(),
				sound,
				category,
				volume,
				pitch,
				id,
				stopOnDeath,
				true
			)
		);
    }

    public static void createAndSendMovingRestrictionLoopingFadingDistanceSound(
		Level level,
		Entity entity,
		Holder<SoundEvent> sound,
		Holder<SoundEvent> sound2,
		SoundSource category,
		float volume,
		float pitch,
		Identifier predicate,
		boolean stopOnDeath,
		float fadeDist,
		float maxDist
	) {
		if (!(level instanceof ServerLevel) || !(entity instanceof EntityLoopingFadingDistanceSoundInterface soundInterface)) return;

		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			ServerPlayNetworking.send(
				player,
				new MovingFadingDistanceSwitchingRestrictionSoundPacket(
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
				)
			);
		}

		if (entity instanceof ServerPlayer player) {
			ServerPlayNetworking.send(
				player,
				new MovingFadingDistanceSwitchingRestrictionSoundPacket(
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
				)
			);
		}

		soundInterface.frozenLib$addFadingDistanceSound(
			sound.unwrapKey().orElseThrow().identifier(),
			sound2.unwrapKey().orElseThrow().identifier(),
			category,
			volume,
			pitch,
			predicate,
			stopOnDeath,
			fadeDist,
			maxDist
		);
    }

    public static void createAndSendMovingRestrictionLoopingFadingDistanceSound(
		ServerPlayer player,
		Entity entity,
		Holder<SoundEvent> sound,
		Holder<SoundEvent> sound2,
		SoundSource category,
		float volume,
		float pitch,
		Identifier predicate,
		boolean stopOnDeath,
		float fadeDist,
		float maxDist
	) {
		ServerPlayNetworking.send(
			player,
			new MovingFadingDistanceSwitchingRestrictionSoundPacket(
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
			)
		);
    }

    public static void createAndSendMovingRestrictionFadingDistanceSound(
		ServerPlayer player,
		Entity entity,
		Holder<SoundEvent> sound,
		Holder<SoundEvent> sound2,
		SoundSource category,
		float volume,
		float pitch,
		Identifier predicate,
		boolean stopOnDeath,
		float fadeDist,
		float maxDist
	) {
		ServerPlayNetworking.send(
			player,
			new MovingFadingDistanceSwitchingRestrictionSoundPacket(
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
			)
		);
    }

    public static void createAndSendFadingDistanceSound(
		Level level,
		Vec3 pos,
		Holder<SoundEvent> sound,
		Holder<SoundEvent> sound2,
		SoundSource category,
		float volume,
		float pitch,
		float fadeDist,
		float maxDist
	) {
		if (!(level instanceof ServerLevel serverLevel)) return;
		for (ServerPlayer player : PlayerLookup.tracking(serverLevel, BlockPos.containing(pos))) {
			ServerPlayNetworking.send(
				player,
				new FadingDistanceSwitchingSoundPacket(
					pos,
					sound,
					sound2,
					category,
					volume,
					pitch,
					fadeDist,
					maxDist
				)
			);
		}
    }

    public static void createAndSendStartingMovingRestrictionLoopingSound(
		Level level,
		Entity entity,
		Holder<SoundEvent> startingSound,
		Holder<SoundEvent> sound,
		SoundSource category,
		float volume,
		float pitch,
		Identifier predicate,
		boolean stopOnDeath
	) {
		if (!(level instanceof ServerLevel) || !(entity instanceof EntityLoopingSoundInterface soundInterface)) return;

		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			ServerPlayNetworking.send(
				player,
				new StartingMovingRestrictionSoundLoopPacket(
					entity.getId(),
					startingSound,
					sound,
					category,
					volume,
					pitch,
					predicate,
					stopOnDeath
				)
			);
		}

		if (entity instanceof ServerPlayer player) {
			ServerPlayNetworking.send(
				player,
				new StartingMovingRestrictionSoundLoopPacket(
					entity.getId(),
					startingSound,
					sound,
					category,
					volume,
					pitch,
					predicate,
					stopOnDeath
				)
			);
		}

		soundInterface.frozenLib$addSound(sound.unwrapKey().orElseThrow().identifier(), category, volume, pitch, predicate, stopOnDeath);
    }

    public static void createAndSendStartingMovingRestrictionLoopingSound(
		ServerPlayer player,
		Entity entity,
		Holder<SoundEvent> startingSound,
		Holder<SoundEvent> sound,
		SoundSource category,
		float volume,
		float pitch,
		Identifier predicate,
		boolean stopOnDeath
	) {
		ServerPlayNetworking.send(
			player,
			new StartingMovingRestrictionSoundLoopPacket(
				entity.getId(),
				startingSound,
				sound,
				category,
				volume,
				pitch,
				predicate,
				stopOnDeath
			)
		);
    }

	public static void createAndSendLocalPlayerSound(ServerPlayer player, Holder<SoundEvent> sound, float volume, float pitch) {
		ServerPlayNetworking.send(player, new LocalPlayerSoundPacket(sound, volume, pitch));
	}

}
