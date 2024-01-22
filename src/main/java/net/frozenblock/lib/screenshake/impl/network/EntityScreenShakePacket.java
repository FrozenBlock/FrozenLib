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

package net.frozenblock.lib.screenshake.impl.network;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record EntityScreenShakePacket(
	int entityId,
	float intensity,
	int duration,
	int falloffStart,
	float maxDistance,
	int ticks
) implements CustomPacketPayload {

	public static final Type<EntityScreenShakePacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("screen_shake_entity_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, EntityScreenShakePacket> CODEC = StreamCodec.ofMember(EntityScreenShakePacket::write, EntityScreenShakePacket::new);

	public EntityScreenShakePacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeFloat(this.intensity());
		buf.writeInt(this.duration());
		buf.writeInt(this.falloffStart());
		buf.writeFloat(this.maxDistance());
		buf.writeInt(this.ticks());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
