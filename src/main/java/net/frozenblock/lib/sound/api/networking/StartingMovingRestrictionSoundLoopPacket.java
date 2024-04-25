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

package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSoundLoop;
import net.frozenblock.lib.sound.api.instances.RestrictedStartingSound;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record StartingMovingRestrictionSoundLoopPacket(int id, SoundEvent startingSound, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation predicateId, boolean stopOnDeath) implements FabricPacket {
	public static final PacketType<StartingMovingRestrictionSoundLoopPacket> PACKET_TYPE = PacketType.create(FrozenNetworking.STARTING_RESTRICTION_LOOPING_SOUND_PACKET, StartingMovingRestrictionSoundLoopPacket::new);

	public StartingMovingRestrictionSoundLoopPacket(@NotNull FriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readResourceLocation(),
			buf.readBoolean()
		);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.startingSound);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
		buf.writeResourceLocation(predicateId);
		buf.writeBoolean(this.stopOnDeath);
	}

	@Environment(EnvType.CLIENT)
	public static <T extends Entity> void receive(@NotNull StartingMovingRestrictionSoundLoopPacket packet, @NotNull LocalPlayer player, PacketSender responseSender) {
		ClientLevel level = player.clientLevel;
		T entity = (T) level.getEntity(packet.id());
		if (entity != null) {
			SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
			Minecraft.getInstance().getSoundManager().play(new RestrictedStartingSound<>(
				entity, packet.startingSound(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(),
				new RestrictedMovingSoundLoop<>(
					entity, packet.sound(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()
				)
			));
		}
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
