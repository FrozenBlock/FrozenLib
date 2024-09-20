/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record FlyBySoundPacket(
	int id,
	SoundEvent sound,
	SoundSource category,
	float volume,
	float pitch
) implements FabricPacket {
	public static final PacketType<FlyBySoundPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("flyby_sound_packet"),
		FlyBySoundPacket::new
	);

	public FlyBySoundPacket(@NotNull FriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat()
		);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public static void receive(@NotNull FlyBySoundPacket packet, @NotNull LocalPlayer player, PacketSender responseSender) {
		ClientLevel level = player.clientLevel;
		Entity entity = level.getEntity(packet.id());
		if (entity != null) {
			FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(packet.pitch(), packet.volume(), packet.category(), packet.sound());
			FlyBySoundHub.addEntity(entity, flyBySound);
		}
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
