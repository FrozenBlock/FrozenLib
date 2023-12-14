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
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

public record LocalPlayerSoundPacket(SoundEvent sound, float volume, float pitch) implements FabricPacket {
	public static final PacketType<LocalPlayerSoundPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("local_player_sound_packet"),
		LocalPlayerSoundPacket::new
	);

	public LocalPlayerSoundPacket(@NotNull FriendlyByteBuf buf) {
		this(
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readFloat(),
			buf.readFloat()
		);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public static void receive(@NotNull LocalPlayerSoundPacket packet, LocalPlayer player, PacketSender responseSender) {
		Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(packet.sound(), SoundSource.PLAYERS, packet.volume(), packet.pitch(), player, player.clientLevel.random.nextLong()));
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
