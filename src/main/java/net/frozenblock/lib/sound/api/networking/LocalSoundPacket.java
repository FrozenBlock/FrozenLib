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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

public record LocalSoundPacket(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay) implements FabricPacket {
	public static final PacketType<LocalSoundPacket> PACKET_TYPE = PacketType.create(FrozenNetworking.LOCAL_SOUND_PACKET, LocalSoundPacket::new);

	public LocalSoundPacket(@NotNull FriendlyByteBuf buf) {
		this(
			buf.readDouble(),
			buf.readDouble(),
			buf.readDouble(),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readBoolean()
		);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
		buf.writeBoolean(this.distanceDelay);
	}

	@Environment(EnvType.CLIENT)
	public static void receive(@NotNull LocalSoundPacket packet, @NotNull LocalPlayer player, PacketSender responseSender) {
		ClientLevel level = player.clientLevel;
		level.playLocalSound(packet.x(), packet.y(), packet.z(), packet.sound(), packet.category(), packet.volume(), packet.pitch(), packet.distanceDelay());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
