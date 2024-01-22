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

package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record FlyBySoundPacket(
	int id,
	Holder<SoundEvent> sound,
	SoundSource category,
	float volume,
	float pitch
) implements CustomPacketPayload {
	public static final Type<FlyBySoundPacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("flyby_sound_packet")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, FlyBySoundPacket> CODEC = StreamCodec.ofMember(FlyBySoundPacket::write, FlyBySoundPacket::new);

	public FlyBySoundPacket(@NotNull RegistryFriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).decode(buf),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat()
		);
	}

	public void write(@NotNull RegistryFriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).encode(buf, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public static void receive(@NotNull FlyBySoundPacket packet, ClientPlayNetworking.Context ctx) {
		ClientLevel level = ctx.player().clientLevel;
		Entity entity = level.getEntity(packet.id());
		if (entity != null) {
			FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(packet.pitch(), packet.volume(), packet.category(), packet.sound().value());
			FlyBySoundHub.addEntity(entity, flyBySound);
		}
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
