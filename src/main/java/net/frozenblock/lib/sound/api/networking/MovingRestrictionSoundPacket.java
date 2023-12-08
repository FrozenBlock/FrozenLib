/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSound;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSoundLoop;
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

public record MovingRestrictionSoundPacket(
	int id,
	SoundEvent sound,
	SoundSource category,
	float volume,
	float pitch,
	ResourceLocation predicateId,
	boolean stopOnDeath,
	boolean looping
) implements FabricPacket {

	public static final PacketType<MovingRestrictionSoundPacket> PACKET_TYPE = PacketType.create(FrozenNetworking.MOVING_RESTRICTION_SOUND_PACKET, MovingRestrictionSoundPacket::new);

	public MovingRestrictionSoundPacket(@NotNull FriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readResourceLocation(),
			buf.readBoolean(),
			buf.readBoolean()
		);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
		buf.writeResourceLocation(predicateId);
		buf.writeBoolean(this.stopOnDeath);
		buf.writeBoolean(this.looping);
	}

	@Environment(EnvType.CLIENT)
	public static <T extends Entity> void receive(@NotNull MovingRestrictionSoundPacket packet, @NotNull LocalPlayer player, PacketSender responseSender) {
		ClientLevel level = player.clientLevel;
		T entity = (T) level.getEntity(packet.id());
		if (entity != null) {
			SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
			if (packet.looping())
				Minecraft.getInstance().getSoundManager().play(new RestrictedMovingSoundLoop<>(entity, packet.sound(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()));
			else
				Minecraft.getInstance().getSoundManager().play(new RestrictedMovingSound<>(entity, packet.sound(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()));
		}
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
